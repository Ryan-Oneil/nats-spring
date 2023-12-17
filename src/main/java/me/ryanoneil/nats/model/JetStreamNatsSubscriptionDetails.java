package me.ryanoneil.nats.model;

import java.lang.reflect.Method;

public class JetStreamNatsSubscriptionDetails extends NatsSubscriptionDetails {
    private final String streamName;
    private final boolean bind;
    private final String durable;
    private final String name;
    private final boolean ordered;

    public JetStreamNatsSubscriptionDetails(String subject, String queueName, Method handler, Object listener, int threads, String streamName, boolean bind, String durable, String name, boolean ordered) {
        super(subject, queueName, handler, listener, threads);
        this.streamName = streamName;
        this.bind = bind;
        this.durable = durable;
        this.name = name;
        this.ordered = ordered;
    }

    public String streamName() {
        return streamName;
    }

    public boolean bind() {
        return bind;
    }

    public String durable() {
        return durable;
    }

    public String name() {
        return name;
    }

    public boolean ordered() {
        return ordered;
    }
}
