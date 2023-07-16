package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import me.ryanoneil.nats.util.NatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsConsumer extends Consumer {

    private final Logger logger = LoggerFactory.getLogger(NatsConsumer.class);

    private final NatsSubscriptionDetails subscriptionDetails;

    public NatsConsumer(NatsSubscriptionDetails subscriptionDetails, Connection connection) {
        super(null, connection);
        this.subscriptionDetails = subscriptionDetails;
    }

    @Override
    public void start()  {
        if (isActive()) {
            return;
        }
        var messageHandler = NatsUtil.createMessageHandler(subscriptionDetails);
        Dispatcher dispatcher = connection.createDispatcher(messageHandler);

        subscription = dispatcher.subscribe(subscriptionDetails.subject(), subscriptionDetails.queueName(), messageHandler);

        if (logger.isInfoEnabled()) {
            logger.info("Connected nats consumer to subject={} with queue={}", subscriptionDetails.subject(), subscriptionDetails.queueName());
        }
    }

}
