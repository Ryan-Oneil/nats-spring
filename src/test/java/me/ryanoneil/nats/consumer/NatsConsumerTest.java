package me.ryanoneil.nats.consumer;


import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Subscription;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//Class needs to be public for accessing methods as part of tests
public class NatsConsumerTest {

    private final Method method = this.getClass().getMethods()[0];

    private final NatsSubscriptionDetails subscriptionDetails = new NatsSubscriptionDetails("test", "test", method, this, 1);

    private final Connection connection = mock(Connection.class);

    private final Subscription subscription = mock(Subscription.class);

    private final Dispatcher dispatcher = mock(Dispatcher.class);

    private final NatsConsumer natsConsumer = spy(new NatsConsumer(subscriptionDetails, connection));

    public void testMethod(String test) {
        throw new MessageHandlerException("MethodRan");
    }

    @BeforeEach
    void setup() {
        Mockito.when(connection.createDispatcher(any())).thenReturn(dispatcher);
        Mockito.when(dispatcher.subscribe(any(), any(), any())).thenReturn(subscription);
    }

    @Test
    void isNotActiveTest() {
        Mockito.when(dispatcher.subscribe(any(), any(), any())).thenReturn(null);
        natsConsumer.start();
        boolean isActive = natsConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isNotActiveNullTest() {
        boolean isActive = natsConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        natsConsumer.start();

        assertTrue(natsConsumer.isActive());
    }

    @Test
    void startWhenActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        natsConsumer.start();
        natsConsumer.start();

        Mockito.verify(dispatcher, times(1)).subscribe(any(), any(), any());
    }

    @Test
    void stopWhenActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        natsConsumer.start();
        natsConsumer.stop();

        Mockito.verify(dispatcher, times(1)).unsubscribe(anyString());
    }

    @Test
    void stopWhenNotActive() {
        Mockito.when(subscription.isActive()).thenReturn(false);

        natsConsumer.stop();

        Mockito.verify(dispatcher, times(0)).unsubscribe(anyString());
    }
}
