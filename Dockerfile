FROM maven:latest AS build
RUN mkdir -p "/workspace"
WORKDIR "/workspace"
COPY pom.xml "/workspace"
COPY "/src" "/workspace/src"
RUN mvn -B package --file pom.xml -Pproduction -DskipTests

FROM openjdk:21-oracle
COPY --from=build "/workspace/target/*.jar" app.jar
EXPOSE 8080
ARG _URL
ENV DB_URL $_URL
ARG _USER
ENV DB_USER $_USER
ARG _PASSWORD
ENV DB_PASSWORD $_PASSWORD
ENTRYPOINT ["java", "-jar", "/app.jar"]