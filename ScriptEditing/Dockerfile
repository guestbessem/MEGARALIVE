FROM openjdk:17
RUN apt-get update && apt-get install -y netcat

# Download wait-for-it.sh script
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ARG JAR_FILE=target/ScriptEditing-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["/wait-for-it.sh", "mysql-megagolive:3306", "--", "java", "-jar", "-Dspring.profiles.active=cloud", "/app.jar"]
