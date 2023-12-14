package me.ryanoneil.nats.consumer;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.config.NatsConfig;
import me.ryanoneil.nats.model.SubscriptionStats;
import me.ryanoneil.nats.producer.NatsMessageProducer;
import me.ryanoneil.nats.sample.DummyListener;
import me.ryanoneil.nats.sample.SamplePojo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {DummyListener.class, NatsConfig.class})
class DummyListenerITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Autowired
    private Connection connection;

    @Autowired
    private NatsListenerAnnotationBeanProcessor processor;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void handleNatsMessageTest() {
        NatsConsumer consumer = (NatsConsumer) processor.getConsumers().get(0);

        NatsMessageProducer<SamplePojo> producer = new NatsMessageProducer<>(new ObjectMapper(), connection);
        producer.createAndSendMessage(new SamplePojo("test"), "natsRequest");

        await()
                .atMost(Duration.ofSeconds(1))
                .until(() -> outContent.toString().trim().contains("Received the following from nats"));

        SubscriptionStats stats = consumer.getStats();

        assertEquals("natsRequest", stats.subject());
        assertEquals("", stats.queueName());
        assertEquals(1, stats.delivered());
    }
}
