package me.ryanoneil.nats.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import io.nats.client.impl.NatsMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import me.ryanoneil.nats.exception.MessageProducerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JetStreamMessageProducer<T> {

    private final Logger logger = LoggerFactory.getLogger(JetStreamMessageProducer.class);
    private final JetStream jetStream;
    private final ObjectMapper mapper;

    public JetStreamMessageProducer(JetStream jetStream, ObjectMapper objectMapper) {
        this.jetStream = jetStream;
        this.mapper = objectMapper;
    }

    public Message createMessage(T data, String subject) {
        try {
            String jsonData = mapper.writeValueAsString(data);

            logger.info("Created the following message {} for the subject {}", jsonData, subject);

            return NatsMessage.builder()
                .subject(subject)
                .data(jsonData, StandardCharsets.UTF_8)
                .build();
        } catch (JsonProcessingException e) {
            throw new MessageProducerException(e);
        }
    }

    public void sendMessage(Message message) {
        try {
            jetStream.publish(message);

            logger.info("Published message to {}", message.getSubject());
        } catch (IOException | JetStreamApiException e) {
            throw new MessageProducerException(e);
        }
    }

    public void createAndSendMessage(T data, String subject) {
        Message message = createMessage(data, subject);

        sendMessage(message);
    }
}
