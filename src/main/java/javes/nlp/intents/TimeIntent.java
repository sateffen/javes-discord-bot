package javes.nlp.intents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import javes.nlp.IIntentExecutor;
import javes.nlp.TrainingSampleStream;

/**
 * Implements an intent executor, that tells the current time.
 */
public class TimeIntent implements IIntentExecutor {
    public String getIntentName() {
        return "time-intent";
    }

    public TrainingSampleStream getTrainigSamples() {
        return new TrainingSampleStream(new String[]{
            "What time is it",
            "Do you have the time",
            "I need the time",
            "What does the clock say"
        });
    }

    public void execute(MessageCreateEvent event, Map<String, String> foundEntities) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(Message::getChannel)
            .flatMap(channel -> {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String answer = "It's " + dateFormat.format(calendar.getTime()) + " o'clock";

                return channel.createMessage(answer);
            })
            .subscribe();
    }
}