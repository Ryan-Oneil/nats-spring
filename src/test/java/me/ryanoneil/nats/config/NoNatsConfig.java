package me.ryanoneil.nats.config;

import me.ryanoneil.nats.actuator.ConsumerMetrics;
import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class NoNatsConfig {

    @Bean
    public ConsumerMetrics consumerMetrics(Optional<NatsListenerAnnotationBeanProcessor> natsListenerAnnotationBeanProcessor,
                                           Optional<JetStreamListenerAnnotationBeanProcessor> jetStreamListenerAnnotationBeanProcessor) {
        return new ConsumerMetrics(natsListenerAnnotationBeanProcessor, jetStreamListenerAnnotationBeanProcessor);
    }

}
