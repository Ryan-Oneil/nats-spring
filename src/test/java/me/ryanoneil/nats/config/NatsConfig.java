package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

@Configuration
@Profile("test")
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

    @PreDestroy
    public void destroy() {
        nats.stop();
    }

}
