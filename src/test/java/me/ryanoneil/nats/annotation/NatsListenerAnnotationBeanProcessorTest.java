package me.ryanoneil.nats.annotation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import java.io.IOException;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.sample.DummyListener;
import me.ryanoneil.nats.sample.MultipleThreadListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NatsListenerAnnotationBeanProcessorTest {

    final Connection connection = Mockito.mock(Connection.class);

    final JetStream jetStream = Mockito.mock(JetStream.class);

    NatsListenerAnnotationBeanProcessor processor;

    DummyListener dummyListener = new DummyListener();

    MultipleThreadListener multipleThreadListener = new MultipleThreadListener();

    @BeforeEach
    void setup() {
        processor = Mockito.spy(new NatsListenerAnnotationBeanProcessor(connection, jetStream));
    }

    @Test
    void postProcessBeforeInitializationTest() {
        Object returnedListener = processor.postProcessBeforeInitialization(dummyListener, "Dummy");

        assertNotNull("Bean is null", returnedListener);
        assertEquals("Listener annotation not detected", 1, processor.getSubscriptionDetails().size());
    }

    @Test
    void postProcessAfterInitialization() {
        processor.postProcessBeforeInitialization(dummyListener, "Dummy");
        processor.postProcessAfterInitialization(dummyListener, "Dummy");

        Mockito.verify(processor, Mockito.times(1)).createStreamConsumer(any());
        assertEquals("Consumers not created", 1, processor.getConsumers().size());
    }

    @Test
    void postProcessAfterInitializationWithError() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.subscribe(any(), any(), any(), any(), anyBoolean(), any())).thenThrow(JetStreamApiException.class);
        processor.postProcessBeforeInitialization(dummyListener, "Dummy");


        assertThrows(MessageHandlerException.class, () -> processor.postProcessAfterInitialization(dummyListener, "Dummy"));
    }

    @Test
    void postProcessAfterInitializationMultipleThreads() {
        processor.postProcessBeforeInitialization(multipleThreadListener, "Multiple");
        processor.postProcessAfterInitialization(multipleThreadListener, "Multiple");

        Mockito.verify(processor, Mockito.times(4)).createStreamConsumer(any());
        assertEquals("Consumers not created", 4, processor.getConsumers().size());
    }
}
