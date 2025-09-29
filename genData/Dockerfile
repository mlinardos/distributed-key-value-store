FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /build
COPY . .
RUN mvn clean package -pl genData -am

FROM openjdk:17-slim
WORKDIR /workspace
COPY --from=build /build/genData/target/kv-data-generator-1.0.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-cp", "/app/app.jar", "io.github.mlinardos.kvstore.generator.genData"]
CMD ["-k", "keyFile.txt", "-n", "1000", "-d", "3", "-l", "10", "-m", "5"]