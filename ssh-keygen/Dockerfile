FROM openjdk:17-jdk-alpine

RUN apk add --no-cache openssl

RUN mkdir -p /ssh-keys

VOLUME ["./ssh-keys"]

CMD openssl rand -base64 32 > /ssh-keys/secret.key