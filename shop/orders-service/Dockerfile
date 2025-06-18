FROM eclipse-temurin:17-jre as runtime

WORKDIR /app

COPY target/orders-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
