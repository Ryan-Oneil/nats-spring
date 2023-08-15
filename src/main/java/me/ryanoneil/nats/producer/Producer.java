package me.ryanoneil.nats.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import me.ryanoneil.nats.exception.MessageProducerException;

import java.nio.charset.StandardCharsets;

public abstract class Producer<T> {

    protected final ObjectMapper mapper;

    protected Producer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Message createMessage(T data, String subject) {
        try {
            String jsonData = mapper.writeValueAsString(data);

            return NatsMessage.builder()
                    .subject(subject)
                    .data(jsonData, StandardCharsets.UTF_8)
                    .build();
        } catch (JsonProcessingException e) {
            throw new MessageProducerException(e);
        }
    }

    public Message createMessage(T data, String subject, String replyTo) {
        try {
            String jsonData = mapper.writeValueAsString(data);

            return NatsMessage.builder()
                    .subject(subject)
                    .data(jsonData, StandardCharsets.UTF_8)
                    .replyTo(replyTo)
                    .build();
        } catch (JsonProcessingException e) {
            throw new MessageProducerException(e);
        }
    }

    public Message createMessage(T data, String subject, Headers headers) {
        try {
            String jsonData = mapper.writeValueAsString(data);

            return NatsMessage.builder()
                    .subject(subject)
                    .data(jsonData, StandardCharsets.UTF_8)
                    .headers(headers)
                    .build();
        } catch (JsonProcessingException e) {
            throw new MessageProducerException(e);
        }
    }

    public Message createMessage(T data, String subject, String replyTo, Headers headers) {
        try {
            String jsonData = mapper.writeValueAsString(data);

            return NatsMessage.builder()
                    .subject(subject)
                    .data(jsonData, StandardCharsets.UTF_8)
                    .replyTo(replyTo)
                    .headers(headers)
                    .build();
        } catch (JsonProcessingException e) {
            throw new MessageProducerException(e);
        }
    }

    public abstract void sendMessage(Message message);

    public void createAndSendMessage(T data, String subject) {
        Message message = createMessage(data, subject);

        sendMessage(message);
    }

    public void createAndSendMessage(T data, String subject, String replyTo) {
        Message message = createMessage(data, subject, replyTo);

        sendMessage(message);
    }

    public void createAndSendMessage(T data, String subject, Headers headers) {
        Message message = createMessage(data, subject, headers);

        sendMessage(message);
    }

    public void createAndSendMessage(T data, String subject, String replyTo, Headers headers) {
        Message message = createMessage(data, subject, replyTo, headers);

        sendMessage(message);
    }

}
