Spring Boot Backend For Frontend Example
---

This example implements the Backend For Frontend Pattern and uses Spring OAuth2 Client, which acts as a OAuth Client uses  
the API provided by the [API](../api) resource server example.

The BFF hosts a mini Browser based app [app.js](./src/main/resources/static/app/app.js) which tunnels all down-stream API  
calls through the backend application.

# Build

```
mvn clean package
```

# Run

Use the `BffApplication` launch configuration or run the following command in a terminal.

```
java -jar target/*.jar
```