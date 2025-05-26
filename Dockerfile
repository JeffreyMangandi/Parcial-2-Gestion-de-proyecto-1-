# Etapa 1: Construcción del JAR con Maven
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Ejecución de la aplicación con JDK ligero
FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

COPY --from=build /app/target/com.app.crud-1.0.jar /app/com.app.crud-1.0.jar

EXPOSE 8090

CMD ["java", "-jar", "com.app.crud-1.0.jar"]
