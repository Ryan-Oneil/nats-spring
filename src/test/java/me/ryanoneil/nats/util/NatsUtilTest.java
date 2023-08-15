package me.ryanoneil.nats.util;

import io.nats.client.Message;
import io.nats.client.MessageHandler;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class NatsUtilTest {

    private final Method method = this.getClass().getMethods()[0];

    private final NatsSubscriptionDetails subscriptionDetails = new NatsSubscriptionDetails("test", "test", method, this, 1);

    public void testMethod(Message test) {
        throw new MessageHandlerException("MethodRan");
    }

    @Test
    void createMessageHandlerTest()  {
        MessageHandler messageHandler = NatsUtil.createMessageHandler(subscriptionDetails);
        Message message = mock(Message.class);
        Mockito.when(message.getData()).thenReturn(new byte[]{'2', '3'});

        Assertions.assertNotNull(messageHandler);
        MessageHandlerException exception = assertThrows(MessageHandlerException.class, () ->  messageHandler.onMessage(message));
        Assertions.assertEquals("me.ryanoneil.nats.exception.MessageHandlerException: MethodRan", exception.getMessage());
    }

}
