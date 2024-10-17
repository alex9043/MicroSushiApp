FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/auth-service && chown -R 1000:1000 /var/log/auth-service

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/auth-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]