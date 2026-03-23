FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve
COPY src/ src/
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir -p uploads/tours
COPY --from=build /app/target/*.jar app.jar
ENV PORT=10000
EXPOSE 10000
ENTRYPOINT ["java", "-Xmx384m", "-Xms128m", "-jar", "app.jar"]
