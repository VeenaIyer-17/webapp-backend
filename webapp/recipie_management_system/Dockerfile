# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine
# Add a volume pointing to /tmp
VOLUME /tmp
# The application's jar file
ARG JAR_FILE=target/ROOT.war
# Add the application's jar to the container
ADD ${JAR_FILE} recipe-demo.jar
# Run the jar file
ENTRYPOINT java -jar \
    -Dspring.datasource.domain=${MYSQL_HOST} \
    -Dspring.datasource.username=${MYSQL_USER} \
    -Dspring.datasource.password=${MYSQL_PASSWORD} \
    /recipe-demo.jar