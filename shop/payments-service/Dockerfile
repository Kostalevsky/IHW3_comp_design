FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/payments-service-1.0.0-SNAPSHOT.jar app.jar

# RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
# USER user

ENTRYPOINT ["java","-jar","/app/app.jar"]