package javes.cp.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import reactor.core.publisher.Mono;

public class PingCommandTest {
    @Test
    public void testGetCommand() {
        PingCommand testInstance = new PingCommand();

        assertEquals("ping", testInstance.getCommand());
    }

    @Test
    public void testExecute() {
        // first create the test instance
        PingCommand testInstance = new PingCommand();
        // then create all mocks
        MessageCreateEvent mockedEvent = mock(MessageCreateEvent.class);
        Message mockedMessage = mock(Message.class);
        MessageChannel mockedMessageChannel = mock(MessageChannel.class);

        // then prepare the mocks
        when(mockedEvent.getMessage()).thenReturn(mockedMessage);
        when(mockedMessage.getChannel()).thenReturn(Mono.just(mockedMessageChannel));
        when(mockedMessageChannel.createMessage(anyString())).thenReturn(Mono.just(mockedMessage));

        // and now execute the actual test method
        testInstance.execute(mockedEvent);

        // and verify everything worked
        verify(mockedMessageChannel).createMessage("Pong!");
    }
}