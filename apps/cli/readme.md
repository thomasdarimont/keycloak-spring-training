Spring Boot CLI Device Flow App 
---

This example demonstrates how to use the [OAuth device flow](https://oauth.net/2/device-flow/) to Authenticate a CLI App on the behalf of the user.

Execute the following and, follow the instructions in the log. 

Browse to the prompted URL http://localhost:9090/auth/realms/training/device?user_code=...  
and use the following credentials to login:  
- Username: user
- Password: user

# Build

```
mvn clean package
```

# Run

Use the `CliApplication` launch configuration or run the following command in a terminal.

```
java -jar target/*.jar
```