package me.ryanoneil.nats.actuator;

import io.nats.client.Connection;
import org.springframework.boot.actuate.health.Health;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class contains tests for the {@code BrokerHealth} class and its methods.
 */
class BrokerHealthTest {

    /**
     * Test the 'health' method when the connection status is 'CONNECTED'.
     */
    @Test
    void testHealthWhenConnected() {
        // Mock the Connection object
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.getStatus()).thenReturn(Connection.Status.CONNECTED);

        // Class to be tested
        BrokerHealth brokerHealth = new BrokerHealth(mockConnection);

        // Expected result
        Health expected = Health.up().withDetail("natsBrokerStatus", Connection.Status.CONNECTED.toString()).build();

        // Verify
        assertEquals(expected, brokerHealth.health(), "The health status didn't match the expected result.");
    }

    /**
     * Test the 'health' method when the connection status is not 'CONNECTED'.
     */
    @Test
    void testHealthWhenDisconnected() {
        // Mock the Connection object
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.getStatus()).thenReturn(Connection.Status.CLOSED);

        // Class to be tested
        BrokerHealth brokerHealth = new BrokerHealth(mockConnection);

        // Expected result
        Health expected = Health.down().withDetail("natsBrokerStatus", Connection.Status.CLOSED.toString()).build();

        // Verify
        assertEquals(expected, brokerHealth.health(), "The health status didn't match the expected result.");
    }
}