# 建立階段
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# 運行階段
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /workspace/target/app.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
