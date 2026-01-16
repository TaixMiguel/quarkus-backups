FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
WORKDIR /app/

# Copy only files needed to download dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Create a cacheable layer for dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src/ src/

# Build the native executable
RUN ./mvnw package -Dnative -DskipTests


FROM registry.access.redhat.com/ubi9/ubi-minimal:9.7
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root --chmod=0755 --from=build /app/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]