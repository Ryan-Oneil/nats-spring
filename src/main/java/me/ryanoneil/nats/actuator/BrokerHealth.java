package me.ryanoneil.nats.actuator;

import io.nats.client.Connection;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class BrokerHealth implements HealthIndicator {

    private final Connection connection;

    public BrokerHealth(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Health health() {
        Connection.Status status = connection.getStatus();

        if (status == Connection.Status.CONNECTED) {
            return Health.up().withDetail("natsBrokerStatus", status.toString()).build();
        }
        return Health.down().withDetail("natsBrokerStatus", status.toString()).build();
    }
}
