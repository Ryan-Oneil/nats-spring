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
import io.nats.client.impl.Headers;
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
    void createAndSendMessageWQithReplyTest() throws JetStreamApiException, IOException {
        jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test", "test");

        Mockito.verify(jetStream, times(1)).publish(any());
    }

    @Test
    void createAndSendMessageWithHeadersTest() throws JetStreamApiException, IOException {
        jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test", new Headers());

        Mockito.verify(jetStream, times(1)).publish(any());
    }

    @Test
    void createAndSendMessageAllTest() throws JetStreamApiException, IOException {
        jetStreamMessageProducer.createAndSendMessage(exampleMessage, "test",  "test", new Headers());

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
        Headers headers = new Headers();

        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createMessage(exampleMessage, "test"));
        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createMessage(exampleMessage, "test", "test"));
        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createMessage(exampleMessage, "test", headers));
        assertThrows(MessageProducerException.class, () ->  jetStreamMessageProducer.createMessage(exampleMessage, "test", "test", headers));
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

    @Test
    void sendMessageWithSubjectTest() throws JetStreamApiException, IOException {
        Message message = jetStreamMessageProducer.createMessage(exampleMessage, "test", "test");

        jetStreamMessageProducer.sendMessage(message);

        Mockito.verify(jetStream, times(1)).publish(any());
    }

    @Test
    void sendMessageTestWithHeaders() throws JetStreamApiException, IOException {
        Message message = jetStreamMessageProducer.createMessage(exampleMessage, "test", new Headers());

        jetStreamMessageProducer.sendMessage(message);

        Mockito.verify(jetStream, times(1)).publish(any());
    }

    @Test
    void sendMessageTestWithReplyAndHeaders() throws JetStreamApiException, IOException {
        Message message = jetStreamMessageProducer.createMessage(exampleMessage, "test", "test", new Headers());

        jetStreamMessageProducer.sendMessage(message);

        Mockito.verify(jetStream, times(1)).publish(any());
    }
}
