FROM openjdk:21-oracle
COPY ./target/*.jar list_app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/list_app.jar" ]