package me.ryanoneil.nats.actuator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nats.client.Connection;
import java.time.Duration;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.config.NatsConfig;
import me.ryanoneil.nats.sample.DummyListener;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {NatsConfig.class, DummyListener.class}, properties = {"management.endpoints.web.exposure.include=*"})
@EnableAutoConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsumerMetricsITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Connection connection;

    @Autowired
    private NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor;

    @Test
    @Order(1)
    void metricsTest() {
        String response = restTemplate.getForObject("/actuator/consumers", String.class);

        assertEquals("[{\"subject\":\"natsRequest\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,"
            + "\"isActive\":true},{\"subject\":\"it.request\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,\"isActive\":true}]", response);
    }

    @Test
    @Order(2)
    void metricsAfterMessageConsumptionTest() {
        connection.publish("natsRequest", "test".getBytes());

        await()
            .atMost(Duration.ofSeconds(5))
            .until(() -> {
                String response = restTemplate.getForObject("/actuator/consumers", String.class);

                return response.contains("{\"subject\":\"natsRequest\",\"queueName\":\"\",\"delivered\":1,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,\"isActive\":true}") &&
                    response.contains("{\"subject\":\"it.request\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,\"isActive\":true}");
            });
    }

    @Test
    @Order(3)
    void metricsConsumerStoppedTest() {
        natsListenerAnnotationBeanProcessor.cleanup();
        String response = restTemplate.getForObject("/actuator/consumers", String.class);

        assertEquals("[{\"subject\":\"natsRequest\",\"queueName\":\"\",\"delivered\":1,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,"
            + "\"isActive\":false},{\"subject\":\"it.request\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,\"isActive\":true}]", response);
    }
}
