package me.ryanoneil.nats.config;

import io.nats.client.*;
import io.nats.client.api.StreamConfiguration;
import jakarta.annotation.PreDestroy;
import me.ryanoneil.nats.sample.DummyListener;
import me.ryanoneil.nats.sample.MultipleThreadListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.io.IOException;

public class NatsConfig {

    public static final Integer NATS_PORT = 4222;

    public static final Integer NATS_MGMT_PORT = 8222;

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
    public JetStream jetStream(Connection connection) throws IOException, JetStreamApiException {
        JetStreamManagement management = connection.jetStreamManagement();
        StreamConfiguration streamConfiguration = StreamConfiguration.builder()
            .name("it")
            .addSubjects("request", "multi")
            .build();

        management.addStream(streamConfiguration);


        return connection.jetStream();
    }

    @Bean
    public DummyListener dummyListener() {
        return new DummyListener();
    }

    @Bean
    @ConditionalOnProperty(
            value="multi.enabled",
            havingValue = "true")
    public MultipleThreadListener multipleThreadListener() {
        return new MultipleThreadListener();
    }

    @PreDestroy
    public void destroy() {
        nats.stop();
    }

}
