FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
WORKDIR /app/

# Copy only files needed to download dependencies
COPY --chown=quarkus:quarkus deployment/app/.mvn .mvn
COPY --chown=quarkus:quarkus deployment/app/mvnw .

COPY --chown=quarkus:quarkus pom.xml .
COPY --chown=quarkus:quarkus core/application/pom.xml core/application/pom.xml
COPY --chown=quarkus:quarkus core/domain/pom.xml core/domain/pom.xml
COPY --chown=quarkus:quarkus deployment/app/pom.xml deployment/app/pom.xml
COPY --chown=quarkus:quarkus driven-adapters/file-system/pom.xml driven-adapters/file-system/pom.xml
COPY --chown=quarkus:quarkus driven-adapters/jpa-persistence/pom.xml driven-adapters/jpa-persistence/pom.xml
COPY --chown=quarkus:quarkus driven-adapters/storage-mega/pom.xml driven-adapters/storage-mega/pom.xml
COPY --chown=quarkus:quarkus driving-adapters/rest-controller/pom.xml driving-adapters/rest-controller/pom.xml

# Create a cacheable layer for dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY --chown=quarkus:quarkus . .

# Build the native executable
RUN ./mvnw package -Dnative -DskipTests -pl :app -am


FROM registry.access.redhat.com/ubi9/ubi-minimal:9.7
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root --chmod=0755 --from=build /app/deployment/app/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]