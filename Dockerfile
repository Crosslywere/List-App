FROM openjdk:21-oracle
RUN ./mvnw clean install
COPY ./target/*.jar list_app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/list_app.jar" ]