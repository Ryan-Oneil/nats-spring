package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Subscription;
import me.ryanoneil.nats.model.SubscriptionStats;

public abstract class Consumer {

    protected final JetStream jetStream;
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

    public void stop() {
        if (!isActive()) {
            return;
        }
        subscription.unsubscribe();
    }

    public SubscriptionStats getStats() {
        String queueName = subscription.getQueueName() == null ? "" : subscription.getQueueName();

        return new SubscriptionStats(subscription.getSubject(), queueName, subscription.getDeliveredCount(),
            subscription.getDroppedCount(), subscription.getPendingMessageCount(), subscription.getPendingMessageLimit(),
                isActive());
    }
}
