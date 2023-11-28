package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import me.ryanoneil.nats.actuator.BrokerHealth;
import me.ryanoneil.nats.actuator.ConsumerMetrics;
import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsAutoConfig {

    private final String hostUrl;

    private final long drainingDuration;

    public NatsAutoConfig(@Value("${nats.url:}") String hostUrl, @Value("${nats.drainDuration:0}") long drainingDuration) {
        this.hostUrl = hostUrl;
        this.drainingDuration = drainingDuration;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(Connection.class)
    @ConditionalOnProperty(prefix = "nats", name="url")
    public Connection connection() throws IOException, InterruptedException {
        return Nats.connect(hostUrl);
    }

    @Bean
    @ConditionalOnProperty(
        value="nats.jetstream.enabled",
        havingValue = "true")
    @ConditionalOnBean(Connection.class)
    public JetStream jetStream() throws IOException, InterruptedException {
        return connection().jetStream();
    }

    @Bean
    @ConditionalOnBean(Connection.class)
    public NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor(Connection connection) {
        return new NatsListenerAnnotationBeanProcessor(connection, Duration.of(drainingDuration, ChronoUnit.SECONDS));
    }

    @Bean
    @ConditionalOnBean({Connection.class, JetStream.class})
    public JetStreamListenerAnnotationBeanProcessor jetStreamListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        return new JetStreamListenerAnnotationBeanProcessor(connection, jetStream, Duration.of(drainingDuration, ChronoUnit.SECONDS));
    }

    @Bean
    @ConditionalOnBean(Connection.class)
    public HealthIndicator brokerHealth(Connection connection) {
        return new BrokerHealth(connection);
    }

    @Bean
    @ConditionalOnBean({NatsListenerAnnotationBeanProcessor.class, JetStreamListenerAnnotationBeanProcessor.class})
    public ConsumerMetrics consumerMetrics(NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor,
        JetStreamListenerAnnotationBeanProcessor jetStreamListenerAnnotationBeanProcessor) {
        return new ConsumerMetrics(natsListenerAnnotationBeanProcessor, jetStreamListenerAnnotationBeanProcessor);
    }

}
