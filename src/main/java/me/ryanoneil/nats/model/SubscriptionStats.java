package me.ryanoneil.nats.model;

public record SubscriptionStats (String subject, String queueName, long delivered, long dropped, long pending,
                                 long pendingLimit, boolean isActive) {
}
