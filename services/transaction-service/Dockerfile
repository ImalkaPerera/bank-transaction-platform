FROM eclipse-temurin:17-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy and build logging-common first
COPY logging-common /app/logging-common
WORKDIR /app/logging-common
RUN mvn clean install -Dmaven.test.skip=true

# Copy and build transaction-service
WORKDIR /app/services/transaction-service
COPY services/transaction-service /app/services/transaction-service
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/transaction-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]