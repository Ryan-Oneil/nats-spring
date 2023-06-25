package me.ryanoneil.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import java.io.IOException;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    value="nats.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class NatsAutoConfig {

    private final String hostUrl;

    public NatsAutoConfig(@Value("${nats.url}") String hostUrl) {
        this.hostUrl = hostUrl;
    }

    @Bean(destroyMethod = "close")
    public Connection connection() throws IOException, InterruptedException {
        return Nats.connect(hostUrl);
    }

    @Bean
    public JetStream jetStream() throws IOException, InterruptedException {
        return connection().jetStream();
    }

    @Bean
    public NatsListenerAnnotationBeanProcessor natsListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        return new NatsListenerAnnotationBeanProcessor(connection, jetStream);
    }

}
