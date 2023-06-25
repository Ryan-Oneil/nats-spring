package me.ryanoneil.nats.consumer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamSubscription;
import io.nats.client.PushSubscribeOptions;
import java.io.IOException;
import java.lang.reflect.Method;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

//Class needs to be public for accessing methods as part of tests
public class JetStreamPullConsumerTest {

    private final Method method = this.getClass().getMethods()[0];

    private final NatsSubscriptionDetails subscriptionDetails = new NatsSubscriptionDetails("test", "test", method, this, "tests", 1);

    private final JetStream jetStream = mock(JetStream.class);

    private final Connection connection = mock(Connection.class);

    private final JetStreamSubscription subscription = mock(JetStreamSubscription.class);

    private final JetStreamPullConsumer jetStreamPullConsumer = spy(new JetStreamPullConsumer(subscriptionDetails, jetStream, connection));

    public void testMethod(String test) {
        throw new MessageHandlerException("MethodRan");
    }

    @BeforeEach
    void setup() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.subscribe(any(), any(), any(), any(), eq(false), any())).thenReturn(subscription);
        Mockito.when(connection.createDispatcher(any())).thenReturn(Mockito.mock(Dispatcher.class));
    }

    @Test
    void buildOptionsTest() {
        PushSubscribeOptions options = jetStreamPullConsumer.buildOptions();

        assertNotNull(options);
        assertEquals("test", options.getDeliverGroup());
    }

    @Test
    void isNotActiveTest() throws JetStreamApiException, IOException, NoSuchMethodException, IllegalAccessException {
        Mockito.when(jetStream.subscribe(any(), any(), any(), any(), anyBoolean(), any())).thenReturn(null);
        jetStreamPullConsumer.start();
        boolean isActive = jetStreamPullConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isNotActiveNullTest() {
        boolean isActive = jetStreamPullConsumer.isActive();

        assertFalse(isActive);
    }

    @Test
    void isActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPullConsumer.start();

        assertTrue(jetStreamPullConsumer.isActive());
    }

    @Test
    void startWhenActive() throws JetStreamApiException, IOException {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPullConsumer.start();
        jetStreamPullConsumer.start();

        Mockito.verify(jetStream, times(1)).subscribe(any(), any(), any(), any(), anyBoolean(), any());
    }

    @Test
    void stopWhenActive() {
        Mockito.when(subscription.isActive()).thenReturn(true);

        jetStreamPullConsumer.start();
        jetStreamPullConsumer.stop();

        Mockito.verify(subscription, times(1)).unsubscribe();
    }

    @Test
    void stopWhenNotActive() {
        Mockito.when(subscription.isActive()).thenReturn(false);

        jetStreamPullConsumer.stop();

        Mockito.verify(subscription, times(0)).unsubscribe();
    }
}
