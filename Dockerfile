FROM maven:3.6.0-jdk-8 AS build
RUN apt-get update
RUN apt install build-essential -y --no-install-recommends
COPY src /home/app/src
COPY pom.xml /home/app
COPY Makefile /home/app
RUN cd /home/app && make build-app

FROM openjdk:8-jdk-slim-buster
VOLUME /tmp
COPY --from=build /home/app/target/*.jar dse-on-k8s-with-java.jar
RUN sh -c 'touch ./dse-on-k8s-with-java.jar'
ENV JAVA_OPTS="-Xms64m -Xmx64m"
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar dse-on-k8s-with-java.jar" ]