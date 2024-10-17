FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/gateway-service && chown -R 1000:1000 /var/log/gateway-service

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/gateway-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]