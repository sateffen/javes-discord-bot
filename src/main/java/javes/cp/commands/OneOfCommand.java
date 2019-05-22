package javes.cp.commands;

import javes.cp.ICommandExecutor;

import java.text.MessageFormat;
import java.util.Random;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * This command decides for one of the given options. The options have to be
 * separated by a comma. The result is packed provided from the RESPONSES
 * property. If the user doesn't provide any options, it'll blame the user for
 * missing options, pretending to be Skipper from the Penguins of Madagascar.
 * 
 * {@code
 *      > !oneof hello, world
 *      < world!
 *      > !oneof pizza, burger, kebab, sushi
 *      < Why not kebab?
 * }
 */
public class OneOfCommand implements ICommandExecutor {
    /**
     * A list of the possible responses. {0} gets formatted with the selected
     * option.
     */
    private static final String[] RESPONSES = new String[] { "I think {0} is a great idea!", "{0}!", "Why not {0}?" };

    public String getCommand() {
        return "oneof";
    }

    public void execute(MessageCreateEvent event) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(message -> {
                String messageContent = message.getContent().get();
                String messageToSend = "I need options Kowalski! Options!";

                if (messageContent.length() > 6) {
                    String[] options = messageContent.substring(6).trim().split(",");

                    if (options.length > 0) {
                        int entryIndex = new Random().nextInt(options.length);
                        int answerIndex = new Random().nextInt(OneOfCommand.RESPONSES.length);
                        String choosenOption = options[entryIndex].trim();
                        String choosenAnswer = OneOfCommand.RESPONSES[answerIndex];

                        messageToSend = MessageFormat.format(choosenAnswer, choosenOption);
                    }
                }

                String finalMessageToSend = messageToSend;
                return message.getChannel()
                    .flatMap(channel -> channel.createMessage(finalMessageToSend));
            })
            .subscribe();
    }
}