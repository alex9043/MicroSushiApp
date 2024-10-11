FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/product-service-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]