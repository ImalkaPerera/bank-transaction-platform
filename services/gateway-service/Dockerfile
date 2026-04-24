FROM eclipse-temurin:17-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Build logging-common first so it is available in the local Maven cache
COPY logging-common /app/logging-common
WORKDIR /app/logging-common
RUN mvn clean install -Dmaven.test.skip=true

# Copy and build gateway-service
WORKDIR /app/services/gateway-service
COPY services/gateway-service /app/services/gateway-service
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]