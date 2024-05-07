FROM openjdk:17
ARG JAR_FILE=target/Api-Gateway-3.2.4.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=cloud", "/app.jar"]
