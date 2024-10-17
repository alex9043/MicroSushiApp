FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/product-service && chown -R 1000:1000 /var/log/product-service

WORKDIR /app

COPY target/product-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/product-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]