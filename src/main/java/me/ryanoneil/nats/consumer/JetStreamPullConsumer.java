package me.ryanoneil.nats.consumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.PushSubscribeOptions;
import java.io.IOException;
import me.ryanoneil.nats.exception.ConsumerCreationException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import me.ryanoneil.nats.util.NatsUtil;

public class JetStreamPullConsumer extends Consumer {
    public JetStreamPullConsumer(NatsSubscriptionDetails subscriptionDetails, JetStream jetStream, Connection connection) {
        super(subscriptionDetails, jetStream, connection);
    }

    @Override
    public void start()  {
        if (isActive()) {
            return;
        }
        PushSubscribeOptions subscribeOptions = buildOptions();

        var messageHandler = NatsUtil.createMessageHandler(subscriptionDetails);
        Dispatcher dispatcher = connection.createDispatcher(messageHandler);

        try {
            subscription = jetStream.subscribe(
                subscriptionDetails.subject(),
                subscriptionDetails.queueName(),
                dispatcher,
                messageHandler,
                false,
                subscribeOptions);
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

}
