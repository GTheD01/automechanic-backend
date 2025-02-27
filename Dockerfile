FROM maven:3.9.8-eclipse-temurin-17 AS build

# Set working directory for the build
WORKDIR /backend

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk

ARG JAR_FILE=target/automechanic-0.0.1-SNAPSHOT.jar

COPY --from=build /backend/${JAR_FILE} automechanic.jar

ENTRYPOINT ["java", "-jar", "/automechanic.jar"]

