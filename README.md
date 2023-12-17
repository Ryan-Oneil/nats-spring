# Nats Spring

## Introduction

An easy-to-use annotation driven Nats implementation for Spring boot

## Features

Out of the box support for NATs and JetStream.

Supports JSON object mapping for listeners and producers.
Out of the box metrics support for registered consumers.
Supports base NATs consumers and JetStream push consumer.
Basic health check endpoint for NATs broker connection.
Supports graceful shutdown of consumers by draining before application shutdown.

## Setup

This project is built by Maven. Java 17 or higher is required.

````
<dependency>
  <groupId>me.ryanoneil</groupId>
  <artifactId>nats-spring</artifactId>
  <version>1.0.0</version>
</dependency>
````

## Dependencies

List what libraries/frameworks/packages your project uses.

Here's how you generally list dependencies:
- Spring boot 3
- Spring actuator
- Nats 
- Java 17

## Usage

### Configuration

- nats.url: The url of your nats broker. Can also include authentication.
- nats.jetstream.enabled: To enable JetStream, defaults to false
- nats.drainDuration: Time in seconds for the application to wait for consumer drain to complete. Defaults to 0 which will cause the application to wait until full drain.

### Metrics
The endpoint is /actuator/consumers

Example:
````
[
  {
    "subject": "natsRequest",
    "queueName": "",
    "delivered": 1,
    "dropped": 0,
    "pending": 0,
    "pendingLimit": 524288,
    "isActive": true
  },
  {
    "subject": "it.request",
    "queueName": "",
    "delivered": 0,
    "dropped": 0,
    "pending": 0,
    "pendingLimit": 524288,
    "isActive": false
  }
]
````

### Health check

/actuator/health/broker

Example:
````
{
  "status": "UP",
  "details": {
    "natsBrokerStatus": "CONNECTED"
  }
}
````

### Nats Consumer

The Nats listener has the following parameters
- Subject: Nats subject to listen to.
- Queue: Nats queue to operate from.
- threads: amount of consumers to spin up.

Example:
````
@NatsListener(subject = "natsRequest")
public void handleMessage(MyMessageObject message) {
    System.out.println("Received the following from nats natsRequest: " + message);
}
````

### JetStream push Consumer

The JetStream listener has the following parameters
- Subject: Nats subject to listen to.
- Queue: Nats queue to operate from.
- Stream: JetStream operating from.
- threads: amount of consumers to spin up.
- bind: if the stream is bound (Refer to NATs documentation).
- Durable: name of the durable consumer.
- Name: name of the consumer.
- Ordered: if the message consumption is ordered.

Example:
````
@JetStreamListener(subject = "request", stream = "it")
public void handleJetStreamMessage(MyMessageObject message) {
    System.out.println("Received the following from jetstream it.request: " + message);
}
````

## Contributing

Contributions are welcome!

## License

This project is licensed under the terms of the MIT License. 