package me.ryanoneil.nats.model;

import java.lang.reflect.Method;

public class JetStreamNatsSubscriptionDetails extends NatsSubscriptionDetails {
    private final String streamName;

    public JetStreamNatsSubscriptionDetails(String subject, String queueName, Method handler, Object listener, int threads, String streamName) {
        super(subject, queueName, handler, listener, threads);
        this.streamName = streamName;
    }

    public String streamName() {
        return streamName;
    }

}
