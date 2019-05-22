package javes.cp.commands;

import javes.cp.ICommandExecutor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

/**
 * This command pongs back to the user who requested the ping.
 * 
 * {@code
 *      > !ping
 *      < Pong!
 * }
 */
public class PingCommand implements ICommandExecutor {
    public String getCommand() {
        return "ping";
    }

    public void execute(MessageCreateEvent event) {
        Mono.just(event)
            .map(MessageCreateEvent::getMessage)
            .flatMap(Message::getChannel)
            .flatMap(channel -> channel.createMessage("Pong!"))
            .subscribe();
    }
}