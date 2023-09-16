package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import me.ryanoneil.nats.actuator.BrokerHealth;
import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsAutoConfig {

    private final String hostUrl;

    public NatsAutoConfig(@Value("${nats.url:}") String hostUrl) {
        this.hostUrl = hostUrl;
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
        return new NatsListenerAnnotationBeanProcessor(connection);
    }

    @Bean
    @ConditionalOnBean({Connection.class, JetStream.class})
    public JetStreamListenerAnnotationBeanProcessor jetStreamListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        return new JetStreamListenerAnnotationBeanProcessor(connection, jetStream);
    }

    @Bean
    @ConditionalOnBean(Connection.class)
    public HealthIndicator brokerHealth(Connection connection) {
        return new BrokerHealth(connection);
    }

}
