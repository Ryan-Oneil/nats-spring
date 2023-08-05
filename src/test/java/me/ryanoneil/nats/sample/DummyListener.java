package me.ryanoneil.nats.sample;

import me.ryanoneil.nats.annotation.JetStreamListener;
import me.ryanoneil.nats.annotation.NatsListener;
import org.springframework.stereotype.Component;

@Component
public class DummyListener {

    public int natsMessageCount = 0;
    public int jetStreamMessageCount = 0;

    @NatsListener(subject = "request")
    public void handleMessage(Test message) {
        System.out.println("Received the following from nats test: " + message);
        natsMessageCount++;
    }

    @JetStreamListener(subject = "request")
    public void handleJetStreamMessage(Test message) {
        System.out.println("Received the following from jetstream test: " + message);
        jetStreamMessageCount++;
    }
}
