package me.ryanoneil.nats.actuator;

import io.nats.client.Connection;
import me.ryanoneil.nats.config.NatsConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = {NatsConfig.class}, properties = {"management.endpoint.health.show-details:always"})
@EnableAutoConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrokerHealthITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Connection connection;

    @Test
    @Order(1)
    void brokerHealthTest() {
        String response = restTemplate.getForObject("/actuator/health/broker", String.class);

        assertEquals("{\"status\":\"UP\",\"details\":{\"natsBrokerStatus\":\"CONNECTED\"}}", response);
    }

    @Test
    @Order(2)
    void brokerDownHealthTest() throws InterruptedException {
        connection.close();

        String response = restTemplate.getForObject("/actuator/health/broker", String.class);

        assertEquals("{\"status\":\"DOWN\",\"details\":{\"natsBrokerStatus\":\"CLOSED\"}}", response);
    }
}
