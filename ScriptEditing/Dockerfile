FROM openjdk:17-jdk-slim

# Update package list and install netcat and wget
RUN apt-get update && apt-get install -y netcat wget

# Download wait-for-it.sh script
RUN wget -O /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /usr/local/bin/wait-for-it.sh

# Copy your application files
COPY target/ScriptEditing-0.0.1-SNAPSHOT.jar /app.jar

# Expose the port your application runs on
EXPOSE 9003

# Command to run your application
CMD ["java", "-jar", "/app.jar"]