package me.ryanoneil.nats.annotation;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import me.ryanoneil.nats.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerBeanProcessor {

    private final Logger logger = LoggerFactory.getLogger(ConsumerBeanProcessor.class);

    protected final List<Consumer> consumers;

    protected final Duration drainDuration;

    public ConsumerBeanProcessor(Duration drainDuration) {
        this.consumers = new ArrayList<>();
        this.drainDuration = drainDuration;
    }

    @PreDestroy
    public void cleanup() {
        consumers.stream()
            .map(consumer -> consumer.stop(drainDuration))
            .forEach(booleanCompletableFuture -> {
                try {
                    booleanCompletableFuture.get();
                } catch (InterruptedException e) {
                    logger.error("Error draining consumers during application shutdown", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.error("Error draining consumers during application shutdown", e);
                }
            });
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }
}
