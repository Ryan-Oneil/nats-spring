package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
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
    public Connection connection() throws IOException, InterruptedException, JetStreamApiException {
        nats = new GenericContainer<>("nats:latest")
            .withExposedPorts(NATS_PORT, NATS_MGMT_PORT)
            .withCommand("-js")
            .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));
        nats.start();

        Connection connection = Nats.connect( new Options.Builder().server("nats://" + nats.getHost() + ":" + nats.getMappedPort(NATS_PORT)).build());

        JetStreamManagement jsm = connection.jetStreamManagement();
        StreamConfiguration streamConfig = StreamConfiguration.builder()
            .name("mediaOptimize")
            .subjects("request", "response-test", "multi")
            .storageType(StorageType.Memory)
            .build();

        jsm.addStream(streamConfig);

        return connection;
    }

    @Bean
    public JetStream jetStream() throws IOException, InterruptedException, JetStreamApiException {
        return connection().jetStream();
    }

    @PreDestroy
    public void destroy() {
        nats.stop();
    }

}
