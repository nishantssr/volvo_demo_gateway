FROM amazoncorretto:11-alpine-jdk
VOLUME /tmp
EXPOSE 8080
COPY volvo-service/target/volvo-service-0.0.1-SNAPSHOT.jar volvo-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/volvo-service-0.0.1-SNAPSHOT.jar"]