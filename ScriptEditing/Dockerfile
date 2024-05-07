FROM openjdk:17
ARG JAR_FILE=target/ScriptEditing-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=cloud", "/app.jar"]
