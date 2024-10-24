FROM amazoncorretto:17

WORKDIR /app

COPY target/tradeguard-api-0.0.1-SNAPSHOT.jar /app/tradeguard-api.jar

EXPOSE 9191
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app/tradeguard-api.jar", "--spring.profiles.active=prod"]

