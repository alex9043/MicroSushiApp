FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/registry-service && chown -R 1000:1000 /var/log/registry-service

WORKDIR /app

COPY target/registry-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/registry-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]