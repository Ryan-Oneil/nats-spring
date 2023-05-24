package me.ryanoneil.nats.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.MessageHandler;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.Subscription;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import me.ryanoneil.nats.util.MethodUtil;

public class JetStreamConsumer {

    private final NatsSubscriptionDetails subscriptionDetails;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JetStream jetStream;
    private final Connection connection;
    private Subscription jetstreamSubscription;

    public JetStreamConsumer(NatsSubscriptionDetails subscriptionDetails, JetStream jetStream, Connection connection) {
        this.subscriptionDetails = subscriptionDetails;
        this.jetStream = jetStream;
        this.connection = connection;

    }

    public MessageHandler createMessageHandler() throws NoSuchMethodException, IllegalAccessException {
        MethodHandle methodHandler = MethodUtil.getMethodHandler(subscriptionDetails.handler());

        return msg -> {
            try {
                msg.ack();
                Object object = objectMapper.readValue(new String(msg.getData()), MethodUtil.getParameterType(subscriptionDetails.handler(), 0));

                methodHandler.invoke(subscriptionDetails.listener(), object);
            } catch (Throwable e) {
                throw new MessageHandlerException(e);
            }
        };
    }

    public void start() throws JetStreamApiException, IOException, NoSuchMethodException, IllegalAccessException {
        if (isActive()) {
            return;
        }
        PushSubscribeOptions subscribeOptions = buildOptions();

        var messageHandler = createMessageHandler();
        Dispatcher dispatcher = connection.createDispatcher(messageHandler);

        jetstreamSubscription = jetStream.subscribe(
            subscriptionDetails.subject(),
            subscriptionDetails.queueName(),
            dispatcher,
            messageHandler,
            false,
            subscribeOptions);
    }

    public PushSubscribeOptions buildOptions() {
        return PushSubscribeOptions.builder()
            .stream(subscriptionDetails.streamName())
            .deliverGroup(subscriptionDetails.queueName())
            .build();
    }

    public boolean isActive() {
        return jetstreamSubscription != null && jetstreamSubscription.isActive();
    }

    public void stop() {
        if (!isActive()) {
            return;
        }
        jetstreamSubscription.unsubscribe();
    }

}
