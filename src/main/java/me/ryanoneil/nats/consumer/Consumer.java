package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Subscription;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import me.ryanoneil.nats.exception.ConsumerDrainingException;
import me.ryanoneil.nats.model.SubscriptionStats;

public abstract class Consumer {

    protected JetStream jetStream;
    protected final Connection connection;
    protected Subscription subscription;

    protected Consumer(JetStream jetStream, Connection connection) {
        this.jetStream = jetStream;
        this.connection = connection;
    }

    public abstract void start();

    public boolean isActive() {
        return subscription != null && subscription.isActive();
    }

    public CompletableFuture<Boolean> stop(Duration drainDuration) {
        if (!isActive()) {
            return CompletableFuture.completedFuture(true);
        }
        Dispatcher dispatcher = subscription.getDispatcher();

        try {
            // A subscription with a dispatcher needs to be unsubscribed from the dispatcher
            if (dispatcher == null) {
                return subscription.drain(drainDuration);
            }
            return dispatcher.drain(drainDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ConsumerDrainingException(e);
        }
    }

    public SubscriptionStats getStats() {
        String queueName = subscription.getQueueName() == null ? "" : subscription.getQueueName();

        return new SubscriptionStats(subscription.getSubject(), queueName, subscription.getDeliveredCount(),
            subscription.getDroppedCount(), subscription.getPendingMessageCount(), subscription.getPendingMessageLimit(),
                isActive());
    }

    public SubscriptionStats getJetStreamStats(String streamSubject) {
        String queueName = subscription.getQueueName() == null ? "" : subscription.getQueueName();

        return new SubscriptionStats(streamSubject, queueName, subscription.getDeliveredCount(),
            subscription.getDroppedCount(), subscription.getPendingMessageCount(), subscription.getPendingMessageLimit(),
            isActive());
    }

}
