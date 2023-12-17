package me.ryanoneil.nats.actuator;

import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.consumer.Consumer;
import me.ryanoneil.nats.model.SubscriptionStats;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Endpoint(id = "consumers")
public class ConsumerMetrics {

    private final Optional<NatsListenerAnnotationBeanProcessor> processor;

    private final Optional<JetStreamListenerAnnotationBeanProcessor> jetStreamProcessor;

    public ConsumerMetrics(Optional<NatsListenerAnnotationBeanProcessor> processor, Optional<JetStreamListenerAnnotationBeanProcessor> jetStreamProcessor) {
        this.processor = processor;
        this.jetStreamProcessor = jetStreamProcessor;
    }

    @ReadOperation
    public List<SubscriptionStats> consumers() {
        List<Consumer> consumers = new ArrayList<>();

        processor.ifPresent(natsListenerAnnotationBeanProcessor -> consumers.addAll(natsListenerAnnotationBeanProcessor.getConsumers()));
        jetStreamProcessor.ifPresent(jetStreamListenerAnnotationBeanProcessor -> consumers.addAll(jetStreamListenerAnnotationBeanProcessor.getConsumers()));

        return consumers.stream().map(Consumer::getStats).toList();
    }
}
