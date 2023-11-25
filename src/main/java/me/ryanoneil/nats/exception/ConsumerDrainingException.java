package me.ryanoneil.nats.exception;

public class ConsumerDrainingException extends RuntimeException {

    public ConsumerDrainingException(Throwable cause) {
        super(cause);
    }
}
