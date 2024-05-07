FROM openjdk:17
ARG JAR_FILE=target/EurekaServer-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=cloud", "/app.jar"]
