package me.ryanoneil.nats.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import me.ryanoneil.nats.exception.MessageProducerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JetStreamMessageProducerTest {

    private final JetStream jetStream = mock(JetStream.class);

    private final ObjectMapper objectMapper = spy(ObjectMapper.class);

    private final JetStreamMessageProducer<String> jetStreamMessageProducer = new JetStreamMessageProducer<>(jetStream, objectMapper);

    private final String exampleMessage = "test";

    @Test
    void createMessageTest() {

        Message message = jetStreamMessageProducer.createMessage(exampleMessage, "example");
        String data = new String(message.getData(), StandardCharsets.UTF_8);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("example", message.getSubject());
        Assertions.assertEquals("\"test\"", data);
    }

    @Test
    void createAndSendMessageTest() throws JetStreamApiException, IOException {
        jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test");

        Mockito.verify(jetStream, times(1)).publish(any());
    }

    @Test
    void createAndSendMessageExceptionTest() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.publish(any())).thenThrow(JetStreamApiException.class);

        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test"));
    }

    @Test
    void createMessageExceptionTest() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createMessage(exampleMessage, "test"));
    }

    @Test
    void createAndSendMessageIOExceptionTest() throws JetStreamApiException, IOException {
        Mockito.when(jetStream.publish(any())).thenThrow(IOException.class);

        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test"));
    }

    @Test
    void sendMessageTest() throws JetStreamApiException, IOException {
        Message message = jetStreamMessageProducer.createMessage(exampleMessage, "test");

        jetStreamMessageProducer.sendMessage(message);

        Mockito.verify(jetStream, times(1)).publish(any());
    }
}
