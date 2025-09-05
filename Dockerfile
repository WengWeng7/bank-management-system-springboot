#
# Build stage
#
FROM maven:3.8.1-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17.0.1-jdk-slim

# Install netcat for wait-for-it.sh
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*
COPY --from=build /home/app/target/bank-management-system-0.0.1-SNAPSHOT.jar  /usr/local/lib/bank-management-system.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/bank-management-system.jar"]