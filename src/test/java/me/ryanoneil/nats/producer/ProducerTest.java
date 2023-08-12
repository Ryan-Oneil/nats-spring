package me.ryanoneil.nats.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProducerTest {

    private static final String DATA_FORMAT = "\"%s\"";
    private final Producer<String> producer = new Producer<>(new ObjectMapper()) {
        @Override
        public void sendMessage(Message message) {

        }
    };

    @Test
    void testCreateMessageWithSubject() {
        String data = "data";
        String subject = "subject";
        Message message = producer.createMessage(data, subject);

        assertNotNull(message);
        assertEquals(subject, message.getSubject());
        assertEquals(String.format(DATA_FORMAT, data), new String(message.getData()));
    }

    @Test
    void testCreateMessageWithSubjectAndReplyTo() {
        String data = "data";
        String subject = "subject";
        String replyTo = "replyTo";
        Message message = producer.createMessage(data, subject, replyTo);

        assertNotNull(message);
        assertEquals(subject, message.getSubject());
        assertEquals(replyTo, message.getReplyTo());
        assertEquals(String.format(DATA_FORMAT, data), new String(message.getData()));
    }

    @Test
    void testCreateMessageWithSubjectAndReplyToAndHeader() {
        String data = "data";
        String subject = "subject";
        String replyTo = "replyTo";
        Headers headers = new Headers();
        headers.add("key", "value");

        Message message = producer.createMessage(data, subject, replyTo, headers);

        assertNotNull(message);
        assertEquals(subject, message.getSubject());
        assertEquals(replyTo, message.getReplyTo());
        assertEquals(String.format(DATA_FORMAT, data), new String(message.getData()));
        assertEquals("value", message.getHeaders().getFirst("key"));
    }

}
