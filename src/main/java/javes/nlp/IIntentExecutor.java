package javes.nlp;

import java.util.Map;

import discord4j.core.event.domain.message.MessageCreateEvent;

/**
 * This interface represents an intent executor. This interface has to be
 * implemented by each intent executor placed in the "javes.nlp.intents"
 * package, else it'll get ignored. This Interfaces covers the executor itself
 * as well as the data supply for the OpenNLP model.
 */
public interface IIntentExecutor {
    /**
     * Returns a unique name for this intent executor. This name is only used for
     * internal purpose to identify the intent executor, and log something useful if
     * something failes.
     * 
     * @return The unique name for the intent executor
     */
    public String getIntentName();

    /**
     * Provides the training samples for the OpenNLP models to use.
     * 
     * @return The training sample stream for OpenNLP.
     */
    public TrainingSampleStream getTrainigSamples();

    /**
     * Executes this intent for given MessageCreateEvent
     * 
     * @param event         The MessageCreateEvent that led to this execution
     * @param foundEntities The entities that were found in given message create
     *                      event
     */
    public void execute(MessageCreateEvent event, Map<String, String> foundEntities);
}
