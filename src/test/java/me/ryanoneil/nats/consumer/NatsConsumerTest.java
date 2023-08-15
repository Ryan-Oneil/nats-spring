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

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void statsTest() {
        Mockito.when(subscription.getSubject()).thenReturn("test");
        Mockito.when(subscription.getQueueName()).thenReturn("test");
        Mockito.when(subscription.getDeliveredCount()).thenReturn(1L);
        Mockito.when(subscription.getDroppedCount()).thenReturn(1L);
        Mockito.when(subscription.getPendingMessageCount()).thenReturn(1L);
        Mockito.when(subscription.getPendingMessageLimit()).thenReturn(1L);
        Mockito.when(subscription.isActive()).thenReturn(true);

        natsConsumer.start();

        var stats = natsConsumer.getStats();

        assertEquals("test", stats.subject());
        assertEquals("test", stats.queueName());
        assertEquals(1L, stats.delivered());
        assertEquals(1L, stats.dropped());
        assertEquals(1L, stats.pending());
        assertEquals(1L, stats.pendingLimit());
    }

    @Test
    void statsNotActiveTest() {
        var stats = natsConsumer.getStats();

        assertEquals("test", stats.subject());
        assertEquals("test", stats.queueName());
        assertEquals(0, stats.delivered());
        assertEquals(0, stats.dropped());
        assertEquals(0, stats.pending());
        assertEquals(0, stats.pendingLimit());
    }
}
