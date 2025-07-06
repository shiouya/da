FROM maven:3.9.9-eclipse-temurin-21 AS build

COPY src /root/src
COPY pom.xml /root

WORKDIR /root
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21

COPY --from=build /root/target/*.jar app.jar

# 聲明要開啟 8080 port
EXPOSE 8080

# 指定運行此 package image 的指令
CMD ["java", "-jar", "app.jar"]