package me.ryanoneil.nats.annotation;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.ryanoneil.nats.consumer.JetStreamConsumer;
import me.ryanoneil.nats.exception.MessageHandlerException;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class NatsListenerAnnotationBeanProcessor implements BeanPostProcessor {

    private final Connection connection;
    private final JetStream jetStream;
    private List<NatsSubscriptionDetails> subscriptionDetails;
    private final List<JetStreamConsumer> consumers;

    public NatsListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        this.connection = connection;
        this.jetStream = jetStream;
        this.subscriptionDetails = new ArrayList<>();
        this.consumers = new ArrayList<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails = Arrays.stream(bean.getClass().getMethods())
            .filter(method -> Objects.nonNull(method.getAnnotation(NatsListener.class)))
            .map(method -> {
                NatsListener natsListener = method.getAnnotation(NatsListener.class);

                return new NatsSubscriptionDetails(natsListener.subject(), natsListener.queue(), method, bean, natsListener.stream(),
                    natsListener.threads());
            })
            .toList();

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails.forEach(natsSubscriptionDetails -> {
            for (int i = 0; i < natsSubscriptionDetails.threads(); i++) {
                consumers.add(createStreamConsumer(natsSubscriptionDetails));
            }
        });
        return bean;
    }

    public JetStreamConsumer createStreamConsumer(NatsSubscriptionDetails subscription) {
        JetStreamConsumer jetStreamConsumer = new JetStreamConsumer(subscription, jetStream, connection);

        try {
            jetStreamConsumer.start();

            return jetStreamConsumer;
        } catch (NoSuchMethodException | IOException | JetStreamApiException | IllegalAccessException e) {
            throw new MessageHandlerException(e);
        }
    }

    @PreDestroy
    public void cleanup() {
        consumers.forEach(JetStreamConsumer::stop);
    }

    public List<NatsSubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

    public List<JetStreamConsumer> getConsumers() {
        return consumers;
    }
}
