package javes.cp;

import discord4j.core.event.domain.message.MessageCreateEvent;

/**
 * This interface represents a command executor. This interface has to be
 * implemented by each command executor placed in the "javes.cp.commands"
 * package, else it'll get ignored. This Interfaces covers the executor itself
 * as well as the command name.
 */
public interface ICommandExecutor {
    /**
     * Returns the command on which this executor should react.
     * 
     * @return The actual command to react to
     */
    public String getCommand();

    /**
     * Executes this command for given MessageCreateEvent
     * 
     * @param event The MessageCreateEvent to react to
     */
    public void execute(MessageCreateEvent event);
}
