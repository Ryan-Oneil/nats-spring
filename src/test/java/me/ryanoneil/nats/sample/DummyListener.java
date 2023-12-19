package me.ryanoneil.nats.sample;

import io.nats.client.Message;
import me.ryanoneil.nats.annotation.JetStreamListener;
import me.ryanoneil.nats.annotation.NatsListener;

public class DummyListener {

    @NatsListener(subject = "natsRequest")
    public void handleMessage(Message message) {
        System.out.println("Received the following from nats test: " + message);
    }

    @JetStreamListener(subject = "request", stream = "it")
    public void handleJetStreamMessage(Message message) {
        System.out.println("Received the following from jetstream test: " + message);
    }
}
