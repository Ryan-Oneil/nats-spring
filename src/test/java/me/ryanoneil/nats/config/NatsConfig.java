package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PreDestroy;
import me.ryanoneil.nats.actuator.BrokerHealth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.io.IOException;

@Configuration
public class NatsConfig {

    private static final Integer NATS_PORT = 4222;

    private static final Integer NATS_MGMT_PORT = 8222;

    private GenericContainer<?> nats;

    @Bean
    public Connection connection() throws IOException, InterruptedException {
        nats = new GenericContainer<>("nats:latest")
            .withExposedPorts(NATS_PORT, NATS_MGMT_PORT)
            .withCommand("-js")
            .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));
        nats.start();

        return Nats.connect( new Options.Builder().server("nats://" + nats.getHost() + ":" + nats.getMappedPort(NATS_PORT)).build());
    }

    @Bean
    public JetStream jetStream() throws IOException, InterruptedException {
        return connection().jetStream();
    }

    @Bean(name = "broker")
    public BrokerHealth brokerHealth(Connection connection) {
        return new BrokerHealth(connection);
    }

    @PreDestroy
    public void destroy() {
        nats.stop();
    }

}
