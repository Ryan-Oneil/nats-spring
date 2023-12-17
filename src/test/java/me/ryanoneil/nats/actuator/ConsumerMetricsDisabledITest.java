package me.ryanoneil.nats.actuator;

import me.ryanoneil.nats.config.NoNatsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {NoNatsConfig.class}, properties = {"management.endpoints.web.exposure.include=*"})
@EnableAutoConfiguration
class ConsumerMetricsDisabledITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void metricsTest() {
        String response = restTemplate.getForObject("/actuator/consumers", String.class);

        assertEquals("[]", response);
    }
}
