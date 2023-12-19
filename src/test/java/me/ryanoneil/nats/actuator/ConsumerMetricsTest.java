package me.ryanoneil.nats.actuator;

import me.ryanoneil.nats.annotation.JetStreamListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.annotation.NatsListenerAnnotationBeanProcessor;
import me.ryanoneil.nats.consumer.Consumer;
import me.ryanoneil.nats.model.SubscriptionStats;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConsumerMetricsTest {

    @MockBean
    private NatsListenerAnnotationBeanProcessor natsProcessor;

    @MockBean
    private JetStreamListenerAnnotationBeanProcessor jetStreamProcessor;

    @MockBean
    private Consumer consumer;

    @Test
    void consumersTest_withConsumers() {
        // arrange
        List<Consumer> consumerList = new ArrayList<>();
        consumerList.add(consumer);
        when(natsProcessor.getConsumers()).thenReturn(consumerList);
        when(jetStreamProcessor.getConsumers()).thenReturn(consumerList);

        SubscriptionStats subscriptionStats = new SubscriptionStats("subject1", "queueName1", 100L, 10L, 50L, 200L, true);
        when(consumer.getStats()).thenReturn(subscriptionStats);

        ConsumerMetrics consumerMetrics = new ConsumerMetrics(Optional.of(natsProcessor),Optional.of(jetStreamProcessor));

        // act
        List<SubscriptionStats> result = consumerMetrics.consumers();

        // assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo(subscriptionStats);
    }

    @Test
    void consumersTest_noConsumers() {
        // arrange
        List<Consumer> consumerList = new ArrayList<>();
        when(natsProcessor.getConsumers()).thenReturn(consumerList);
        when(jetStreamProcessor.getConsumers()).thenReturn(consumerList);

        ConsumerMetrics consumerMetrics = new ConsumerMetrics(Optional.of(natsProcessor),Optional.of(jetStreamProcessor));

        // act
        List<SubscriptionStats> result = consumerMetrics.consumers();

        // assert
        assertThat(result).isEmpty();
    }

    @Test
    void consumersTest_processorOptionalsNotPresent() {
        // arrange
        ConsumerMetrics consumerMetrics = new ConsumerMetrics(Optional.empty(),Optional.empty());

        // act
        List<SubscriptionStats> result = consumerMetrics.consumers();

        // assert
        assertThat(result).isEmpty();
    }

    @Test
    void consumersTest_jetStreamPresentButNatsAbsent() {
        // arrange
        List<Consumer> jetStreamConsumerList = new ArrayList<>();
        jetStreamConsumerList.add(consumer);
        when(jetStreamProcessor.getConsumers()).thenReturn(jetStreamConsumerList);

        SubscriptionStats subscriptionStats = new SubscriptionStats("subject2", "queueName2", 200L, 20L, 100L, 400L, false);
        when(consumer.getStats()).thenReturn(subscriptionStats);

        ConsumerMetrics consumerMetrics = new ConsumerMetrics(Optional.empty(),Optional.of(jetStreamProcessor));

        // act
        List<SubscriptionStats> result = consumerMetrics.consumers();

        // assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo(subscriptionStats);
    }
}