package me.ryanoneil.nats.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ryanoneil.nats.config.NatsConfig;
import me.ryanoneil.nats.sample.DummyListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {NatsConfig.class, DummyListener.class}, properties = {"management.endpoints.web.exposure.include=*"})
@EnableAutoConfiguration
class ConsumerMetricsITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void metricsTest() {
        String response = restTemplate.getForObject("/actuator/consumers", String.class);

        assertEquals("[{\"subject\":\"request\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,"
            + "\"isActive\":true},{\"subject\":\"it.request\",\"queueName\":\"\",\"delivered\":0,\"dropped\":0,\"pending\":0,\"pendingLimit\":524288,\"isActive\":true}]", response);
    }
}
