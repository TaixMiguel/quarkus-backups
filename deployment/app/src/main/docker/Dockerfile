FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
WORKDIR /app/

# Copy Maven wrapper files first
COPY --chown=quarkus:quarkus deployment/app/.mvn deployment/app/.mvn
COPY --chown=quarkus:quarkus deployment/app/mvnw deployment/app/mvnw

# Copy all POM files for dependency resolution (cacheable layer)
COPY --chown=quarkus:quarkus pom.xml .
COPY --chown=quarkus:quarkus core/application/pom.xml core/application/
COPY --chown=quarkus:quarkus core/domain/pom.xml core/domain/
COPY --chown=quarkus:quarkus deployment/app/pom.xml deployment/app/
COPY --chown=quarkus:quarkus driven-adapters/file-system/pom.xml driven-adapters/file-system/
COPY --chown=quarkus:quarkus driven-adapters/jpa-persistence/pom.xml driven-adapters/jpa-persistence/
COPY --chown=quarkus:quarkus driven-adapters/storage-mega/pom.xml driven-adapters/storage-mega/
COPY --chown=quarkus:quarkus driving-adapters/rest-controller/pom.xml driving-adapters/rest-controller/

# Download external dependencies (this caches external deps)
RUN deployment/app/mvnw dependency:go-offline -f pom.xml

# Now copy all source code
COPY --chown=quarkus:quarkus core/ core/
COPY --chown=quarkus:quarkus driven-adapters/ driven-adapters/
COPY --chown=quarkus:quarkus driving-adapters/ driving-adapters/
COPY --chown=quarkus:quarkus deployment/app/src deployment/app/src

# Build and install all internal modules first (this makes them available as dependencies)
RUN deployment/app/mvnw clean install -DskipTests -f pom.xml

# Build the native executable
RUN deployment/app/mvnw package -Dnative -DskipTests -f deployment/app/pom.xml

# Runtime stage
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.7
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

COPY --chown=1001:root --chmod=0755 --from=build /app/deployment/app/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]