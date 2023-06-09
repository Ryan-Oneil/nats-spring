package me.ryanoneil.nats.sample;

import me.ryanoneil.nats.annotation.NatsListener;
import org.springframework.stereotype.Component;

@Component
public class DummyListener {

    @NatsListener(subject = "request")
    public void handleMessage(Test message) {
        System.out.println("Received the following from test: " + message);
    }
}
