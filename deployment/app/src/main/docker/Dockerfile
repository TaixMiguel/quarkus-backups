FROM registry.access.redhat.com/ubi9/openjdk-21:1.20 AS build

# Install Maven and required tools
USER 0
RUN microdnf install -y maven && microdnf clean all

WORKDIR /app/

# Copy all POM files for dependency resolution (cacheable layer)
COPY pom.xml .
COPY core/application/pom.xml core/application/
COPY core/domain/pom.xml core/domain/
COPY deployment/app/pom.xml deployment/app/
COPY driven-adapters/file-system/pom.xml driven-adapters/file-system/
COPY driven-adapters/jpa-persistence/pom.xml driven-adapters/jpa-persistence/
COPY driven-adapters/mqtt-publisher/pom.xml driven-adapters/mqtt-publisher/
COPY driven-adapters/storage-local/pom.xml driven-adapters/storage-local/
COPY driven-adapters/storage-mega/pom.xml driven-adapters/storage-mega/
COPY driving-adapters/rest-controller/pom.xml driving-adapters/rest-controller/

# Copy all source code
COPY core/ core/
COPY driven-adapters/ driven-adapters/
COPY driving-adapters/ driving-adapters/
COPY deployment/app/src deployment/app/src

# Build everything using system Maven
RUN mvn -B clean install -DskipTests -f pom.xml

# Runtime stage
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