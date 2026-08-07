FROM amazoncorretto:21-alpine-jdk
COPY target/stats-server.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]