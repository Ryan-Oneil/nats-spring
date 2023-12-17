package me.ryanoneil.nats.config;

import io.nats.client.*;
import io.nats.client.api.StreamConfiguration;
import jakarta.annotation.PreDestroy;
import me.ryanoneil.nats.actuator.BrokerHealth;
import me.ryanoneil.nats.actuator.ConsumerMetrics;
import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Configuration
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
    public NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor(Connection connection) {
        return new NatsListenerAnnotationBeanProcessor(connection, Duration.of(0, ChronoUnit.SECONDS));
    }

    @Bean
    public JetStreamListenerAnnotationBeanProcessor jetStreamListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        return new JetStreamListenerAnnotationBeanProcessor(connection, jetStream, Duration.of(0, ChronoUnit.SECONDS));
    }

    @Bean(name = "broker")
    public BrokerHealth brokerHealth(Connection connection) {
        return new BrokerHealth(connection);
    }

    @Bean
    public ConsumerMetrics consumerMetrics(Optional<NatsListenerAnnotationBeanProcessor> natsListenerAnnotationBeanProcessor,
                                           Optional<JetStreamListenerAnnotationBeanProcessor> jetStreamListenerAnnotationBeanProcessor) {
        return new ConsumerMetrics(natsListenerAnnotationBeanProcessor, jetStreamListenerAnnotationBeanProcessor);
    }

    @PreDestroy
    public void destroy() {
        nats.stop();
    }

}
