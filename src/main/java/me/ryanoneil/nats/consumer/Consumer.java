package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Subscription;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;

public abstract class Consumer {

    protected final NatsSubscriptionDetails subscriptionDetails;
    protected final JetStream jetStream;
    protected final Connection connection;
    protected Subscription subscription;

    protected Consumer(NatsSubscriptionDetails subscriptionDetails, JetStream jetStream, Connection connection) {
        this.subscriptionDetails = subscriptionDetails;
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
}
