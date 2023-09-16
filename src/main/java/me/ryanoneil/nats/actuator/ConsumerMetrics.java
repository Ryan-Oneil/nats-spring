package me.ryanoneil.nats.actuator;

import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.consumer.Consumer;
import me.ryanoneil.nats.model.SubscriptionStats;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.List;

@Endpoint(id = "consumers")
public class ConsumerMetrics {

    private final NatsListenerAnnotationBeanProcessor processor;

    private final JetStreamListenerAnnotationBeanProcessor jetStreamProcessor;

    public ConsumerMetrics(NatsListenerAnnotationBeanProcessor processor, JetStreamListenerAnnotationBeanProcessor jetStreamProcessor) {
        this.processor = processor;
        this.jetStreamProcessor = jetStreamProcessor;
    }

    @ReadOperation
    public List<SubscriptionStats> metrics() {
        List<Consumer> consumers = processor.getConsumers();
        consumers.addAll(jetStreamProcessor.getConsumers());

        return consumers.stream().map(Consumer::getStats).toList();
    }
}
