FROM maven:latest AS build
RUN mkdir -p "/workspace"
WORKDIR "/workspace"
COPY pom.xml "/workspace"
COPY "/src" "/workspace/src"
RUN mvn -B package --file pom.xml -DskipTests

FROM openjdk:21-oracle
COPY --from=build "/workspace/target/*.jar" app.jar
EXPOSE 8080
ARG DB_URL
ENV DB_URL $DB_URL
ARG DB_USER
ENV DB_USER $DB_USER
ARG DB_PASSWORD
ENV DB_PASSWORD $DB_PASSWORD
ENTRYPOINT ["java", "-jar", "/app.jar"]