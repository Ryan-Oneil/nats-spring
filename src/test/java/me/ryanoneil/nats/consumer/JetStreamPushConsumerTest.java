package me.ryanoneil.nats.consumer;


import io.nats.client.*;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.JetStreamNatsSubscriptionDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//Class needs to be public for accessing methods as part of tests
public class JetStreamPushConsumerTest {

    private final Method method = this.getClass().getMethods()[0];

    private final JetStreamNatsSubscriptionDetails subscriptionDetails = new JetStreamNatsSubscriptionDetails("test", "test", method, this, 1, "tests",
            false, "", "", false);

    private final JetStream jetStream = mock(JetStream.class);

    private final Connection connection = mock(Connection.class);

    private final JetStreamSubscription subscription = mock(JetStreamSubscription.class);

    private final Dispatcher dispatcher = mock(Dispatcher.class);

    private final JetStreamPushConsumer jetStreamPushConsumer = spy(new JetStreamPushConsumer(subscriptionDetails, jetStream, connection));

    public void testMethod(String test) {
        throw new MessageHandlerException("MethodRan");
    }

    @BeforeEach
    void setup() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.subscribe(any(), any(), any(), any(), eq(false), any())).thenReturn(subscription);
        Mockito.when(connection.createDispatcher(any())).thenReturn(dispatcher);
    }

    @Test
    void buildOptionsTest() {
        PushSubscribeOptions options = jetStreamPushConsumer.buildOptions();

        assertNotNull(options);
        assertEquals(subscriptionDetails.streamName(), options.getStream());
        assertEquals(subscriptionDetails.bind(), options.isBind());
        assertEquals(subscriptionDetails.ordered(), options.isOrdered());
    }

    @Test
    void isNotActiveTest() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.subscribe(any(), any(), any(), any(), anyBoolean(), any())).thenReturn(null);
        jetStreamPushConsumer.start();
        boolean isActive = jetStreamPushConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isNotActiveNullTest() {
        boolean isActive = jetStreamPushConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPushConsumer.start();

        assertTrue(jetStreamPushConsumer.isActive());
    }

    @Test
    void startWhenActive() throws JetStreamApiException, IOException {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPushConsumer.start();
        jetStreamPushConsumer.start();

        Mockito.verify(jetStream, times(1)).subscribe(any(), any(), any(), any(), anyBoolean(), any());
    }

    @Test
    void stopWhenActive() throws InterruptedException {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPushConsumer.start();
        jetStreamPushConsumer.stop(Duration.ZERO);

        Mockito.verify(dispatcher, times(1)).drain(any());
    }

    @Test
    void stopWhenNotActive() throws InterruptedException {
        Mockito.when(subscription.isActive()).thenReturn(false);

        jetStreamPushConsumer.stop(Duration.ZERO);

        Mockito.verify(dispatcher, times(0)).drain(any());
    }
}
