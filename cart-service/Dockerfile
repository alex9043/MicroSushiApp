FROM openjdk:17-jdk-alpine

RUN mkdir -p /var/log/cart-service && chown -R 1000:1000 /var/log/cart-service

WORKDIR /app

COPY target/cart-service-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME /var/log/cart-service

ENTRYPOINT ["java", "-jar", "/app/app.jar"]