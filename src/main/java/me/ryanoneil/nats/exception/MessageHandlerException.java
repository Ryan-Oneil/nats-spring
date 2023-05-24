package me.ryanoneil.nats.exception;

public class MessageHandlerException extends RuntimeException {

    public MessageHandlerException(Throwable cause) {
        super(cause);
    }

    public MessageHandlerException(String message) {
        super(message);
    }
}
