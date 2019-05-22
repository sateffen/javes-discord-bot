package javes.cp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.reflections.Reflections;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * This is the command processing controller. It's used as consumer of discords
 * MessageCreateEvents and uses a simple string comparison to determine, if the
 * user wants to execute a known command. If the command is actually known, the
 * corresponding command executor is called. All commands start with
 * "CPController.COMMAND_OPENER"
 */
public class CPController implements Consumer<MessageCreateEvent> {
    /**
     * A constant telling about the command opener character. This is prepended to
     * all commands. Only commands with this opener are executed.
     */
    private static final String COMMAND_OPENER = "!";

    /**
     * The command map, mapping each command to it's command executor instance.
     */
    private Map<String, ICommandExecutor> commandMap;

    /**
     * Finds the list of classes implementing the ICommandExecutor interface in the
     * subpackage "javes.cp.commands". Returns an instance of each class in a set.
     * 
     * @return A set containing an instance of all intent executors known
     */
    public static Set<ICommandExecutor> getCommandExecutors() {
        Reflections commandsReflection = new Reflections("javes.cp.commands");
        Set<ICommandExecutor> commandExecutors = new HashSet<ICommandExecutor>();

        Set<Class<? extends ICommandExecutor>> potentialCommandExecutors = commandsReflection.getSubTypesOf(ICommandExecutor.class);

        for (Class<? extends ICommandExecutor> potentialCommandExecutor : potentialCommandExecutors) {
            try {
                ICommandExecutor commandExecutor = (ICommandExecutor) potentialCommandExecutor.getDeclaredConstructor().newInstance();
                commandExecutors.add(commandExecutor);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }

        return commandExecutors;
    }

    /**
     * Creates the actual CPController. Adds each known command to the commandMap.
     */
    public CPController() {
        this.commandMap = new HashMap<String, ICommandExecutor>();
        Set<ICommandExecutor> commandList = CPController.getCommandExecutors();

        for (ICommandExecutor command : commandList) {
            this.commandMap.put(CPController.COMMAND_OPENER + command.getCommand().toLowerCase(), command);
        }
    }

    /**
     * Processes a MessageCreateEvent sent from discord. This is needed to implement
     * the Consumer<MessageCreateEvent> interface. Will filter out messages that
     * have no content or are sent by a bot.
     */
    public void accept(MessageCreateEvent event) {
        Mono.just(event).map(MessageCreateEvent::getMessage)
                .filter(message -> message.getContent().isPresent() && message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .map(message -> message.getContent().get())
                .filter(messageContent -> messageContent.startsWith(CPController.COMMAND_OPENER))
                .subscribe(messageContent -> {
                    String[] tokens = messageContent.split(" ", 2);

                    if (tokens.length > 0) {
                        String calledCommand = tokens[0].toLowerCase();

                        if (this.commandMap.containsKey(calledCommand))
                            this.commandMap.get(calledCommand).execute(event);
                    }
                });
    }
}