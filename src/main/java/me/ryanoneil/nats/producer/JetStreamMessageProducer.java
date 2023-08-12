package me.ryanoneil.nats.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import me.ryanoneil.nats.exception.MessageProducerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JetStreamMessageProducer<T> extends Producer<T> {

    private final Logger logger = LoggerFactory.getLogger(JetStreamMessageProducer.class);
    private final JetStream jetStream;

    public JetStreamMessageProducer(JetStream jetStream, ObjectMapper objectMapper) {
        super(objectMapper);
        this.jetStream = jetStream;
    }

    public void sendMessage(Message message) {
        try {
            jetStream.publish(message);

            logger.info("Published message to {}", message.getSubject());
        } catch (IOException | JetStreamApiException e) {
            throw new MessageProducerException(e);
        }
    }

}
