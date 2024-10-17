FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/account-service && chown -R 1000:1000 /var/log/account-service

WORKDIR /app

COPY target/account-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/account-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]