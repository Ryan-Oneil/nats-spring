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

### Metrics

Metrics are enabled by default if the Connection & JetStream bean is created.

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

## Contributing

Contributions are welcome!

## License

This project is licensed under the terms of the MIT License. 