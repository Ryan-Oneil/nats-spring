package me.ryanoneil.nats.model;

import java.lang.reflect.Method;

public class NatsSubscriptionDetails {

    private final String subject;
    private final String queueName;
    private final Method handler;
    private final Object listener;
    private final int threads;

    public NatsSubscriptionDetails(String subject, String queueName, Method handler, Object listener, int threads) {
        this.subject = subject;
        this.queueName = queueName;
        this.handler = handler;
        this.listener = listener;
        this.threads = threads;
    }

    public String subject() {
        return subject;
    }

    public String queueName() {
        return queueName;
    }

    public Method handler() {
        return handler;
    }

    public Object listener() {
        return listener;
    }

    public int threads() {
        return threads;
    }

    @Override
    public String toString() {
        return "SubscriptionDetails[" +
            "subject=" + subject + ", " +
            "queueName=" + queueName + ", " +
            "handler=" + handler + ", " +
            "listener=" + listener + ", " +
            "threads=" + threads + ']';
    }

}
