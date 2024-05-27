Keycloak Spring Training Labs
---

# Importing

In IntelliJ click on the [pom.xml](./pom.xml) and choose `Add as Maven project`.

# Build

In the beginning some tests will fail during build. Therefore we run the following command 
to create the initial artefacts.

The tests will work once the labs have been completed.

```bash
mvn clean package -DskipTests
```