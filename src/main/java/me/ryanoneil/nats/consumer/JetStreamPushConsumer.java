package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.PushSubscribeOptions;
import me.ryanoneil.nats.exception.ConsumerCreationException;
import me.ryanoneil.nats.model.JetStreamNatsSubscriptionDetails;
import me.ryanoneil.nats.model.SubscriptionStats;
import me.ryanoneil.nats.util.NatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JetStreamPushConsumer extends Consumer {

    private final Logger logger = LoggerFactory.getLogger(JetStreamPushConsumer.class);

    private final JetStreamNatsSubscriptionDetails subscriptionDetails;

    public JetStreamPushConsumer(JetStreamNatsSubscriptionDetails subscriptionDetails, JetStream jetStream, Connection connection) {
        super(jetStream, connection);
        this.subscriptionDetails = subscriptionDetails;
    }

    @Override
    public void start()  {
        if (isActive()) {
            return;
        }
        PushSubscribeOptions subscribeOptions = buildOptions();

        var messageHandler = NatsUtil.createMessageHandler(subscriptionDetails);
        dispatcher = connection.createDispatcher(messageHandler);

        try {
            subscription = jetStream.subscribe(
                subscriptionDetails.subject(),
                subscriptionDetails.queueName(),
                dispatcher,
                messageHandler,
                false,
                subscribeOptions);

            if (logger.isInfoEnabled()) {
                logger.info("Connected jet stream consumer to steam={} subject={} with queue={}", subscriptionDetails.streamName(),
                    subscriptionDetails.subject(), subscriptionDetails.queueName());
            }
        } catch (JetStreamApiException | IOException e) {
            throw new ConsumerCreationException(e);
        }
    }

    public PushSubscribeOptions buildOptions() {
        return PushSubscribeOptions.builder()
            .stream(subscriptionDetails.streamName())
            .deliverGroup(subscriptionDetails.queueName())
            .build();
    }

    @Override
    public SubscriptionStats getStats() {
        return super.getJetStreamStats("%s.%s".formatted(subscriptionDetails.streamName(), subscriptionDetails.subject()));
    }
}
