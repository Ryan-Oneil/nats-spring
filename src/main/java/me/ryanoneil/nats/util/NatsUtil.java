package me.ryanoneil.nats.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.MessageHandler;
import java.lang.invoke.MethodHandle;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;

public class NatsUtil {

    private NatsUtil() {}

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MessageHandler createMessageHandler(NatsSubscriptionDetails natsSubscriptionDetails)  {
        try {
            MethodHandle methodHandler = MethodUtil.getMethodHandler(natsSubscriptionDetails.handler());

            return msg -> {
                try {
                    msg.ack();
                    Object object = objectMapper.readValue(new String(msg.getData()), MethodUtil.getParameterType(natsSubscriptionDetails.handler(), 0));

                    methodHandler.invoke(natsSubscriptionDetails.listener(), object);
                } catch (Throwable e) {
                    throw new MessageHandlerException(e);
                }
            };
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new MessageHandlerException(e);
        }
    }
}
