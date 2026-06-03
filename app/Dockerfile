FROM gradle:8.10.2-jdk21 AS build

WORKDIR /workspace

COPY settings.gradle.kts build.gradle.kts ./
COPY src ./src

RUN gradle --no-daemon --console=plain clean test bootJar

FROM build AS runtime

WORKDIR /app
USER root

RUN mkdir -p /data \
    && chown -R 1000:1000 /app /data

USER 1000:1000

ENV SERVER_PORT=8080

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "--add-modules", "java.net.http", "-jar", "/app/app.jar"]
