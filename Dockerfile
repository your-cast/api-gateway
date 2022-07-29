FROM maven:latest as builder

MAINTAINER Dmytro Kuchura

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/

RUN mvn install -DskipTests

FROM openjdk:17.0.2-slim

WORKDIR /app
COPY --from=builder /build/target/your-cast-api-gateway-0.0.1.jar /app/

ENTRYPOINT ["java", "-jar", "your-cast-api-gateway-0.0.1.jar"]