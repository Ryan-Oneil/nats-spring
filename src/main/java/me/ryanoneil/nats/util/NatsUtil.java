package me.ryanoneil.nats.util;

import io.nats.client.MessageHandler;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;

import java.lang.invoke.MethodHandle;

public class NatsUtil {

    private NatsUtil() {}

    public static MessageHandler createMessageHandler(NatsSubscriptionDetails natsSubscriptionDetails)  {
        try {
            MethodHandle methodHandler = MethodUtil.getMethodHandler(natsSubscriptionDetails.handler());

            return msg -> {
                try {
                    msg.ack();

                    methodHandler.invoke(natsSubscriptionDetails.listener(), msg);
                } catch (Throwable e) {
                    throw new MessageHandlerException(e);
                }
            };
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new MessageHandlerException(e);
        }
    }
}
