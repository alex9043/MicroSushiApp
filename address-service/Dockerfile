FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/address-service && chown -R 1000:1000 /var/log/address-service

WORKDIR /app

COPY target/address-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/address-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]