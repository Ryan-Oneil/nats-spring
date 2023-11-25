package me.ryanoneil.nats.annotation;

import io.nats.client.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.ryanoneil.nats.consumer.NatsConsumer;
import me.ryanoneil.nats.model.NatsSubscriptionDetails;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class NatsListenerAnnotationBeanProcessor extends ConsumerBeanProcessor implements BeanPostProcessor {

    private final Connection connection;
    private List<NatsSubscriptionDetails> subscriptionDetails;

    public NatsListenerAnnotationBeanProcessor(Connection connection, Duration drainDuration) {
        super(drainDuration);
        this.connection = connection;
        this.subscriptionDetails = new ArrayList<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails = Arrays.stream(bean.getClass().getMethods())
            .filter(method -> Objects.nonNull(method.getAnnotation(NatsListener.class)))
            .map(method -> {
                NatsListener natsListener = method.getAnnotation(NatsListener.class);

                return new NatsSubscriptionDetails(natsListener.subject(), natsListener.queue(), method, bean,
                    natsListener.threads());
            })
            .toList();

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        subscriptionDetails.forEach(natsSubscriptionDetails -> {
            for (int i = 0; i < natsSubscriptionDetails.threads(); i++) {
                consumers.add(createNatsConsumer(natsSubscriptionDetails));
            }
        });
        return bean;
    }

    public NatsConsumer createNatsConsumer(NatsSubscriptionDetails subscription) {
        NatsConsumer natsConsumer = new NatsConsumer(subscription, connection);
        natsConsumer.start();

        return natsConsumer;
    }

    public List<NatsSubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

}
