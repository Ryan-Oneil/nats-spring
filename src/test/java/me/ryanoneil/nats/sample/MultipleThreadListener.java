package me.ryanoneil.nats.sample;

import me.ryanoneil.nats.annotation.JetStreamListener;
import me.ryanoneil.nats.annotation.NatsListener;
import org.springframework.stereotype.Component;

@Component
public class MultipleThreadListener {

    @NatsListener(subject = "multi", threads = 4)
    public void handleMessage(SamplePojo message) {
        System.out.println("Received the following from test: " + message);
    }

    @JetStreamListener(subject = "multi", threads = 4)
    public void handleJetStreamMessage(SamplePojo message) {
        System.out.println("Received the following from test: " + message);
    }
}
