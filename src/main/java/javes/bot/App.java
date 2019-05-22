package javes.bot;

import java.util.Map;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import javes.cp.CPController;
import javes.nlp.NLPController;

public class App {
    /**
     * Constant that tells the name for the discord token environment variable
     */
    private static final String TOKEN_ENVIRONMENT_VARIABLE = "JAVES_DISCORD_TOKEN";

    /**
     * The main function, startingpoint for the bot
     * 
     * @param args The commandline arguments passed
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Map<String, String> env = System.getenv();

        if (!env.containsKey(App.TOKEN_ENVIRONMENT_VARIABLE)) {
            throw new Exception("Can't find discord token in environment variable '" + App.TOKEN_ENVIRONMENT_VARIABLE + "'");
        }

        // First read the discord token and create the discord client
        String discordToken = env.get(App.TOKEN_ENVIRONMENT_VARIABLE);
        DiscordClient client = new DiscordClientBuilder(discordToken).build();

        // Attach Command Processor
        client.getEventDispatcher()
            .on(MessageCreateEvent.class)
            .subscribe(new CPController());

        // Attach Natural Language Processor
        client.getEventDispatcher()
            .on(MessageCreateEvent.class)
            .subscribe(new NLPController());

        // Register shutdown hook for clean logout
        Runtime.getRuntime()
            .addShutdownHook(new Thread() {
                public void run() {
                    client.logout().block();
                    System.out.println("Logged out successfully");
                }
            });

        // Now everything is done, so
        client.login().block();
    }
}
