# Your Cast API gateway

Your Cast API gateway is a Java (Spring Boot) application for proxy routes via microservices.

# Setup

Use the package manager [maven](https://maven.apache.org) to build an artifact and install payments processing service. Ensure that all db migration have been performed (if persistence layer is available).

```
maven mvn clean install
```

Service should be accessible on [localhost:8081](localhost:8081).

# Libraries

Regarding keep our services consistent, please use libraries listed below, accordingly to their usecases.
Application has been written in Java 13th version so feel free to use any modern features it brings.

### Lombok
Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java.

[Check documentation](https://projectlombok.org)

### Spring Cloud Gateway
Library for building an API Gateway on top of Spring WebFlux.
Spring Cloud Gateway aims to provide a simple, yet effective way to route to APIs and provide cross cutting concerns to them such as: security, monitoring/metrics, and resiliency.

[Check documentation](https://spring.io/projects/spring-cloud-gateway)

### netty.io
Netty is a NIO client server framework which enables quick and easy development of network applications such as protocol servers and clients.
It greatly simplifies and streamlines network programming such as TCP and UDP socket server.

[Check documentation](https://netty.io)

# Contributing

- Dmytro Kuchura