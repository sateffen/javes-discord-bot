package javes.nlp.intents;

import java.net.URLEncoder;
import java.util.Map;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import javes.nlp.IIntentExecutor;
import javes.nlp.TrainingSampleStream;

/**
 * This intent is a simple "what"-intent that generates wikipedia links of
 * questioned stuff. The main purpose is to test out the entity tagging.
 */
public class WhatIntent implements IIntentExecutor {
    public String getIntentName() {
        return "what-intent";
    }

    public TrainingSampleStream getTrainigSamples() {
        return new TrainingSampleStream(new String[]{
            "What is <START:kind> sand <END> ?",
            "What is <START:kind> a book <END> ?",
            "What is <START:kind> paper <END> ?",
            "What is <START:kind> a city <END> ?",
            "What is <START:kind> a bottle of water <END> ?",
            "What is <START:kind> music <END> ?",
            "Where is <START:kind> london <END> ?",
            "Where is <START:kind> berlin <END> ?",
            "Where is <START:kind> paris <END> ?",
            "Where is <START:kind> germany <END> ?",
            "Where are <START:kind> my keys <END> ?",
            "Where are <START:kind> the rocky mountains <END> ?",
            "Who is <START:kind> Peter Jackson <END>",
            "Who is <START:kind> the Hulk <END>",
            "Who is <START:kind> Homer Simpson <END>"
        });
    }

    public void execute(MessageCreateEvent event, Map<String, String> foundEntities) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(Message::getChannel)
            .flatMap(channel -> {
                System.out.println("Here we go");
                String answer = "I don't get that.";

                if (foundEntities.containsKey("kind")) {
                    try {
                        String encodedMessageContent = URLEncoder.encode(foundEntities.get("kind"), "UTF-8");
                        answer = "https://en.wikipedia.org/w/index.php?title=Special:Search&search=" + encodedMessageContent;
                    }
                    catch (Exception e) {
                        answer = "I'm confused...";
                    }
                }

                System.out.println(answer);
                return channel.createMessage(answer);
            })
            .subscribe();
    } 
}