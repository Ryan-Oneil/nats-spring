package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import static me.ryanoneil.nats.config.NatsConfig.NATS_PORT;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "nats.jetstream.enabled=true")
@EnableAutoConfiguration
public class NatsAutoConfigTest {

    private static GenericContainer<?> nats;

    @BeforeAll
    public static void setup() {
        nats = new GenericContainer<>("nats:latest")
            .withExposedPorts(NATS_PORT)
            .withCommand("-js")
            .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));
        nats.start();
    }

    @AfterAll
    public static void cleanup() {
        nats.stop();
    }

    @DynamicPropertySource
    static void natsProperties(DynamicPropertyRegistry registry) {
        registry.add("nats.url", () -> "nats://" + nats.getHost() + ":" + nats.getMappedPort(NATS_PORT));
    }

    @Autowired
    private Connection connection;

    @Autowired
    private JetStream jetStream;

    @Autowired
    private NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor;

    @Autowired
    private JetStreamListenerAnnotationBeanProcessor jetStreamListenerAnnotationBeanProcessor;

    @Autowired
    private HealthIndicator broker;

    @Test
    void notNull() {
        assertNotNull("Object was null", connection);
        assertNotNull("Object was null", jetStream);
        assertNotNull("Object was null", natsListenerAnnotationBeanProcessor);
        assertNotNull("Object was null", jetStreamListenerAnnotationBeanProcessor);
        assertNotNull("Object was null", broker);
    }
}
