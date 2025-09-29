FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /build

# Copy entire project
COPY . .

# Build kvServer with all dependencies
RUN mvn clean package -pl kvServer -am

# Runtime stage
FROM openjdk:17-slim

WORKDIR /app

# Copy the fat JAR with dependencies
COPY --from=build /build/kvServer/target/kv-server-1.0.0-SNAPSHOT-jar-with-dependencies.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["-a", "0.0.0.0", "-p", "8000"]