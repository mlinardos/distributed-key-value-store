FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /build
COPY . .
RUN mvn clean package -pl kvClient -am

FROM openjdk:17-slim
WORKDIR /workspace
COPY --from=build /build/kvClient/target/kv-client-1.0.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
CMD ["-s", "serverFile.txt", "-i", "dataToIndex.txt", "-k", "2"]