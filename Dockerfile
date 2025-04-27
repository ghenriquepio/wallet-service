FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy as runtime

WORKDIR /app

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .

COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

CMD ["java", "-jar", "app.jar"]