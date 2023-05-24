package me.ryanoneil.nats.model;

import java.lang.reflect.Method;

public record NatsSubscriptionDetails(String subject, String queueName, Method handler, Object listener, String streamName, int threads) {

}
