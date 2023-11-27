package me.ryanoneil.nats.annotation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import io.nats.client.Subscription;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.ryanoneil.nats.consumer.Consumer;
import me.ryanoneil.nats.sample.DummyListener;
import me.ryanoneil.nats.sample.MultipleThreadListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NatsListenerAnnotationBeanProcessorTest {

    final Connection connection = Mockito.mock(Connection.class);

    NatsListenerAnnotationBeanProcessor processor;

    DummyListener dummyListener = new DummyListener();

    MultipleThreadListener multipleThreadListener = new MultipleThreadListener();

    Dispatcher dispatcher;

    Subscription subscription;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() {
        dispatcher = Mockito.mock(Dispatcher.class);
        subscription = Mockito.mock(Subscription.class);
        Mockito.when(subscription.getDispatcher()).thenReturn(dispatcher);
        Mockito.when(dispatcher.subscribe(anyString(), (MessageHandler) any())).thenReturn(subscription);
        Mockito.when(connection.createDispatcher(any())).thenReturn(dispatcher);
        processor = Mockito.spy(new NatsListenerAnnotationBeanProcessor(connection, Duration.ZERO));

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
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

        Mockito.verify(processor, Mockito.times(1)).createNatsConsumer(any());
        assertEquals("Consumers not created", 1, processor.getConsumers().size());
    }

    @Test
    void postProcessAfterInitializationMultipleThreads() {
        processor.postProcessBeforeInitialization(multipleThreadListener, "Multiple");
        processor.postProcessAfterInitialization(multipleThreadListener, "Multiple");

        Mockito.verify(processor, Mockito.times(4)).createNatsConsumer(any());
        assertEquals("Consumers not created", 4, processor.getConsumers().size());
    }

    @Test
    void cleanUpAfterInitialization() {
        processor.postProcessBeforeInitialization(dummyListener, "Dummy");
        processor.postProcessAfterInitialization(dummyListener, "Dummy");

        List<Consumer> consumers = processor.getConsumers();

        Mockito.verify(processor, Mockito.times(1)).createNatsConsumer(any());
        assertEquals("Consumers not created", 1, processor.getConsumers().size());

        processor.cleanup();

        assertFalse("Consumer not stopped", consumers.get(0).isActive());
    }

    @Test
    void cleanUpAfterInitializationThrowsInterruptedException() throws InterruptedException, ExecutionException {
        processor.postProcessBeforeInitialization(dummyListener, "Dummy");
        processor.postProcessAfterInitialization(dummyListener, "Dummy");
        Mockito.when(subscription.isActive()).thenReturn(true);
        CompletableFuture completableFuture = Mockito.mock(CompletableFuture.class);
        Mockito.when(completableFuture.get()).thenThrow(InterruptedException.class);
        Mockito.when(dispatcher.drain(any())).thenReturn(completableFuture);

        processor.cleanup();

        assertTrue("Clean up did not throw exception", outContent.toString().trim().contains("Error draining consumers during application shutdown"));
    }

    @Test
    void cleanUpAfterInitializationThrowsExecutionExceptionException() throws InterruptedException, ExecutionException {
        processor.postProcessBeforeInitialization(dummyListener, "Dummy");
        processor.postProcessAfterInitialization(dummyListener, "Dummy");
        Mockito.when(subscription.isActive()).thenReturn(true);
        CompletableFuture completableFuture = Mockito.mock(CompletableFuture.class);
        Mockito.when(completableFuture.get()).thenThrow(ExecutionException.class);
        Mockito.when(dispatcher.drain(any())).thenReturn(completableFuture);

        processor.cleanup();

        assertTrue("Clean up did not throw exception", outContent.toString().trim().contains("Error draining consumers during application shutdown"));
    }
}
