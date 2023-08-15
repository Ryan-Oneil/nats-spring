package me.ryanoneil.nats.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsMessageProducer<T> extends Producer<T> {

    private final Logger logger = LoggerFactory.getLogger(NatsMessageProducer.class);

    private final Connection connection;

    public NatsMessageProducer(ObjectMapper mapper, Connection connection) {
        super(mapper);
        this.connection = connection;
    }

    @Override
    public void sendMessage(Message message) {
        connection.publish(message);

        if (logger.isInfoEnabled()) {
            logger.info("Published message to {}", message.getSubject());
        }
    }
}
