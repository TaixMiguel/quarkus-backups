FROM registry.access.redhat.com/ubi9/openjdk-21:1.20 AS build

# Install required tools for Maven wrapper
USER 0
RUN microdnf install -y gzip tar && microdnf clean all

WORKDIR /app/

# Copy Maven wrapper files first
COPY deployment/app/.mvn deployment/app/.mvn
COPY deployment/app/mvnw deployment/app/mvnw

# Copy all POM files for dependency resolution (cacheable layer)
COPY pom.xml .
COPY core/application/pom.xml core/application/
COPY core/domain/pom.xml core/domain/
COPY deployment/app/pom.xml deployment/app/
COPY driven-adapters/file-system/pom.xml driven-adapters/file-system/
COPY driven-adapters/jpa-persistence/pom.xml driven-adapters/jpa-persistence/
COPY driven-adapters/storage-mega/pom.xml driven-adapters/storage-mega/
COPY driving-adapters/rest-controller/pom.xml driving-adapters/rest-controller/

# Download dependencies (this caches dependencies)
RUN deployment/app/mvnw dependency:go-offline -f pom.xml

# Copy all source code
COPY core/ core/
COPY driven-adapters/ driven-adapters/
COPY driving-adapters/ driving-adapters/
COPY deployment/app/src deployment/app/src

# Build all modules first (install to local repo), then build the app
RUN deployment/app/mvnw clean install -DskipTests -f pom.xml

# Runtime stage - much smaller image
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.20
ENV LANGUAGE='en_US:en'
WORKDIR /work/

# Copy the built artifacts
COPY --chown=185 --from=build /app/deployment/app/target/quarkus-app/lib/ /work/lib/
COPY --chown=185 --from=build /app/deployment/app/target/quarkus-app/*.jar /work/
COPY --chown=185 --from=build /app/deployment/app/target/quarkus-app/app/ /work/app/
COPY --chown=185 --from=build /app/deployment/app/target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080
USER 185

ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "/work/quarkus-run.jar"]