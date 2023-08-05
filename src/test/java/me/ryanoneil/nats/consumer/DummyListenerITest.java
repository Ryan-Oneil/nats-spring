package me.ryanoneil.nats.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nats.client.Connection;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.config.NatsConfig;
import me.ryanoneil.nats.sample.DummyListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {DummyListener.class, NatsConfig.class, NatsListenerAnnotationBeanProcessor.class})
@SpringBootTest
class DummyListenerITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Autowired
    private DummyListener dummyListener;

    @Autowired
    private Connection connection;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void handleMessageTest() throws InterruptedException {
        connection.publish("request", "{\"example\":\"test\"}".getBytes());

        Thread.sleep(1000L);

        assertEquals("Received the following from nats test: Test{example='test'}", outContent.toString().trim());
    }
}
