package me.ryanoneil.nats.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.config.NatsConfig;
import me.ryanoneil.nats.producer.NatsMessageProducer;
import me.ryanoneil.nats.sample.DummyListener;
import me.ryanoneil.nats.sample.SamplePojo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.awaitility.Awaitility.await;

@ContextConfiguration(classes = {DummyListener.class, NatsConfig.class, NatsListenerAnnotationBeanProcessor.class})
@SpringBootTest
class DummyListenerITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Autowired
    private Connection connection;

    private NatsMessageProducer<SamplePojo> producer;

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
        producer = new NatsMessageProducer<>(new ObjectMapper(), connection);
        producer.createAndSendMessage(new SamplePojo("test"), "request");

        await()
                .atMost(Duration.ofSeconds(1))
                .until(() -> outContent.toString().trim().equals("Received the following from nats test: Test{example='test'}"));
    }
}
