package me.ryanoneil.nats.annotation;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.ryanoneil.nats.consumer.Consumer;
import me.ryanoneil.nats.consumer.JetStreamPushConsumer;
import me.ryanoneil.nats.model.JetStreamNatsSubscriptionDetails;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class JetStreamListenerAnnotationBeanProcessor implements BeanPostProcessor {

    private final Connection connection;
    private final JetStream jetStream;
    private List<JetStreamNatsSubscriptionDetails> subscriptionDetails;
    private final List<Consumer> consumers;

    public JetStreamListenerAnnotationBeanProcessor(Connection connection, JetStream jetStream) {
        this.connection = connection;
        this.jetStream = jetStream;
        this.subscriptionDetails = new ArrayList<>();
        this.consumers = new ArrayList<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails = Arrays.stream(bean.getClass().getMethods())
            .filter(method -> Objects.nonNull(method.getAnnotation(JetStreamListener.class)))
            .map(method -> {
                JetStreamListener streamListener = method.getAnnotation(JetStreamListener.class);

                return new JetStreamNatsSubscriptionDetails(streamListener.subject(), streamListener.queue(), method, bean,
                    streamListener.threads(), streamListener.stream());
            })
            .toList();

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails.forEach(natsSubscriptionDetails -> {
            for (int i = 0; i < natsSubscriptionDetails.threads(); i++) {
                consumers.add(createPushStreamConsumer(natsSubscriptionDetails));
            }
        });
        return bean;
    }

    public JetStreamPushConsumer createPushStreamConsumer(JetStreamNatsSubscriptionDetails subscription) {
        JetStreamPushConsumer jetStreamPushConsumer = new JetStreamPushConsumer(subscription, jetStream, connection);

        jetStreamPushConsumer.start();

        return jetStreamPushConsumer;
    }

    @PreDestroy
    public void cleanup() {
        consumers.forEach(Consumer::stop);
    }

    public List<JetStreamNatsSubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }
}
