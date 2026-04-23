FROM eclipse-temurin:17-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy and build logging-common first (from root context)
COPY logging-common /app/logging-common
WORKDIR /app/logging-common
RUN mvn clean install -DskipTests

# Copy and build account-service
WORKDIR /app/services/account-service
COPY services/account-service /app/services/account-service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/account-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]