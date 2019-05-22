package javes.cp.commands;

import java.net.URLEncoder;

import discord4j.core.event.domain.message.MessageCreateEvent;
import javes.cp.ICommandExecutor;
import reactor.core.publisher.Mono;

/**
 * This command represents a link generator for google. It'll generate a search
 * link for given terms.
 * 
 * {@code
 *      > !google hello world
 *      < https://www.google.com/search?q=hello+world
 *      > !google
 *      < https://www.google.com
 * }
 */
public class GoogleCommand implements ICommandExecutor {
    public String getCommand() {
        return "google";
    }

    public void execute(MessageCreateEvent event) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(message -> {
                try {
                    String messageContent = message.getContent().get();
                    String[] messageTokens = messageContent.split(" ", 2);
                    String messageToSend = "https://www.google.com";

                    if (messageTokens.length == 2) {
                        String encodedMessageContent = URLEncoder.encode(messageTokens[1], "UTF-8");
                        messageToSend = "https://www.google.com/search?q=" + encodedMessageContent;
                    }

                    String finalMessageToSend = messageToSend;
                    return message.getChannel()
                    .flatMap(channel -> channel.createMessage(finalMessageToSend));
                }
                catch (Exception e) {
                    System.out.println(e);

                    return Mono.error(e);
                }
            })
            .subscribe();
    }
}