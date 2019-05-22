package javes.cp.commands;

import javes.cp.ICommandExecutor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * This command echos back the content that follows the command
 * 
 * {@code
 *      > !echo hello world
 *      < hello world
 * }
 */
public class EchoCommand implements ICommandExecutor {
    public String getCommand() {
        return "echo";
    }

    public void execute(MessageCreateEvent event) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(message -> {
                String messageContent = message.getContent().get();
                String messageToSend = "ECHO... Echo... echo...";

                if (messageContent.length() > 5) {
                    messageToSend = messageContent.substring(5).trim();
                }

                String finalMessageToSend = messageToSend;
                return message.getChannel()
                    .flatMap(channel -> channel.createMessage(finalMessageToSend));
            })
            .subscribe();
    }
}