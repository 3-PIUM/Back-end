FROM openjdk:17-slim
COPY app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]