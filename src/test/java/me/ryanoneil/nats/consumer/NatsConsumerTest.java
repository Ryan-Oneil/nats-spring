package me.ryanoneil.nats.consumer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Subscription;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.ryanoneil.nats.exception.ConsumerDrainingException;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        Mockito.when(subscription.getDispatcher()).thenReturn(dispatcher);
    }

    @Test
    void isNotActiveTest() {
        Mockito.when(dispatcher.subscribe(any(), any(), any())).thenReturn(subscription);
        Mockito.when(subscription.isActive()).thenReturn(false);
        natsConsumer.start();

        boolean isActive = natsConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isNotActiveNullTest() {
        Mockito.when(dispatcher.subscribe(any(), any(), any())).thenReturn(null);
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
    void stopWhenActive() throws InterruptedException {
        Mockito.when(subscription.isActive()).thenReturn(true);

        natsConsumer.start();
        natsConsumer.stop(Duration.ZERO);

        Mockito.verify(dispatcher, times(1)).drain(any());
    }

    @Test
    void stopWhenActiveNoDispatcher() throws InterruptedException, ExecutionException {
        Mockito.when(subscription.isActive()).thenReturn(true);
        Mockito.when(subscription.getDispatcher()).thenReturn(null);
        Mockito.when(subscription.drain(any())).thenReturn(CompletableFuture.completedFuture(true));

        natsConsumer.start();
        CompletableFuture<Boolean> drained = natsConsumer.stop(Duration.ZERO);

        assertNotNull(drained);
        assertTrue(drained.get());
        Mockito.verify(subscription, times(1)).drain(any());
    }

    @Test
    void stopWhenNotActive() {
        Mockito.when(subscription.isActive()).thenReturn(false);

        natsConsumer.stop(Duration.ZERO);

        Mockito.verify(dispatcher, times(0)).unsubscribe(anyString());
    }

    @Test
    void stopWhenActiveExceptionThrown() throws InterruptedException {
        Mockito.when(subscription.isActive()).thenReturn(true);
        Mockito.when(dispatcher.drain(any())).thenThrow(InterruptedException.class);
        natsConsumer.start();

        ConsumerDrainingException exception = assertThrows(ConsumerDrainingException.class, () ->  natsConsumer.stop(Duration.ZERO));

        Mockito.verify(dispatcher, times(0)).unsubscribe(anyString());

        assertTrue(Thread.interrupted());
        Assertions.assertEquals("java.lang.InterruptedException", exception.getMessage());
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
