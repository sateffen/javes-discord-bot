package javes.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.reflections.Reflections;

import discord4j.core.event.domain.message.MessageCreateEvent;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamUtils;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import reactor.core.publisher.Mono;

/**
 * This is the natural language processing controller. It's used as consumer of
 * discords MessageCreateEvents, and uses NLP to get the intent of such
 * messages. Only messages where the bot is mentioned are actually processed.
 * Only one intent is executed for one message, and it'll get only executed, if
 * the intent-score is higher than 0.5
 */
public class NLPController implements Consumer<MessageCreateEvent> {
    private static final double MIN_CATEGORIZE_THRESHHOLD = 0.5;

    /**
     * This is the actual document categorizer. This is fed with a created model and
     * categorizes given strings.
     */
    private DocumentCategorizerME categorizer;
    private NameFinderME entityFinder;

    /**
     * This map holds the list of intent executors, mapping their internal intent
     * name to the actual intent executor instance.
     */
    private Map<String, IIntentExecutor> intentMap;

    /**
     * Finds the list of classes implementing the IIntentExecutor interface in the
     * subpackage "javes.nlp.intents". Returns an instance of each class in a set.
     * 
     * @return A set containing an instance of all intent executors known
     */
    public static Set<IIntentExecutor> getIntentExecutors() {
        Reflections commandsReflection = new Reflections("javes.nlp.intents");
        Set<IIntentExecutor> intentExecutors = new HashSet<IIntentExecutor>();

        Set<Class<? extends IIntentExecutor>> potentialIntentExecutors = commandsReflection.getSubTypesOf(IIntentExecutor.class);

        for (Class<? extends IIntentExecutor> potentialIntentExecutor : potentialIntentExecutors) {
            try {
                IIntentExecutor intentExecutor = (IIntentExecutor) potentialIntentExecutor.getDeclaredConstructor().newInstance();
                intentExecutors.add(intentExecutor);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }

        System.out.println(intentExecutors.size());
        return intentExecutors;
    }

    /**
     * Creats the actual NLP controller. This trains the needed OpenNLP categorizer
     * model with all found intent executors.
     * 
     * @throws Exception
     */
    public NLPController() throws Exception {
        this.intentMap = new HashMap<String, IIntentExecutor>();
        List<ObjectStream<DocumentSample>> intentStreams = new ArrayList<ObjectStream<DocumentSample>>();
        List<ObjectStream<NameSample>> entityStreams = new ArrayList<ObjectStream<NameSample>>();
        Set<IIntentExecutor> intentList = NLPController.getIntentExecutors();

        for (IIntentExecutor intent : intentList) {
            ObjectStream<DocumentSample> documentSampleStream = new DocumentSampleStream(intent.getIntentName(), intent.getTrainigSamples());
            ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(intent.getTrainigSamples());

            intentStreams.add(documentSampleStream);
            entityStreams.add(nameSampleStream);
            this.intentMap.put(intent.getIntentName(), intent);
        }

        ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils.concatenateObjectStream(intentStreams);
        ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils.concatenateObjectStream(entityStreams);

        TrainingParameters trainingParams = new TrainingParameters();
        trainingParams.put(TrainingParameters.ITERATIONS_PARAM, 100);
        trainingParams.put(TrainingParameters.CUTOFF_PARAM, 0);

        DoccatModel intentModel = DocumentCategorizerME.train("lang", combinedDocumentSampleStream, trainingParams, new DoccatFactory());
        combinedDocumentSampleStream.close();

        TokenNameFinderModel entityFinderModel = NameFinderME.train("lang", null, combinedNameSampleStream, trainingParams, new TokenNameFinderFactory());
        combinedNameSampleStream.close();

        this.categorizer = new DocumentCategorizerME(intentModel);
        this.entityFinder = new NameFinderME(entityFinderModel);
    }

    /**
     * Processes a MessageCreateEvent sent from discord. This is needed to implement
     * the Consumer<MessageCreateEvent> interface. Will filter out messages that
     * have no content or do not mention this bot directly.
     */
    public void accept(MessageCreateEvent event) {
        Mono.just(event).map(MessageCreateEvent::getMessage)
                .filter(message -> message.getContent().isPresent() && message.getUserMentionIds().contains(event.getClient().getSelfId().get()))
                .subscribe(message -> {
                    String preparedMessage = message.getContent().get().toLowerCase();
                    String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(preparedMessage);
                    double[] results = this.categorizer.categorize(tokens);
                    
                    String intent = this.categorizer.getBestCategory(results);
                    int intentIndex = this.categorizer.getIndex(intent);

                    if (results[intentIndex] > NLPController.MIN_CATEGORIZE_THRESHHOLD) {
                        Map<String, String> foundEntities = new HashMap<String, String>();
                        Span[] spans = this.entityFinder.find(tokens);
                        String[] entities = Span.spansToStrings(spans, tokens);

                        for (int i = 0; i < entities.length; i++) {
                            foundEntities.put(spans[i].getType(), entities[i]);
                        }

                        this.intentMap.get(intent).execute(event, foundEntities);
                    }
                });
    }
}