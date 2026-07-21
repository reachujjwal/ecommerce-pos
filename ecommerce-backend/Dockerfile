FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/retailhub.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
