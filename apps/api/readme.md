API Resource Server
---

A Spring Boot app acting as a resource server which exposes an API.

# Build 

```
mvn clean package
```

# Run

Use the `ApiApplication` launch configuration or run the following command in a terminal. 

```
java -jar target/*.jar
```

# Misc

## curl examples

Request new Access Token for user
```bash
KC_ISSUER="http://localhost:9090/auth/realms/training"
echo "Request new Access Token for user"
KC_RESPONSE=$( \
    curl \
     -s \
     -d "grant_type=password" \
     -d "username=user" \
     -d "password=user" \
     -d "client_id=training-tests" \
     -d "client_secret=secret" \
     -d "scope=profile training" \
      $KC_ISSUER/protocol/openid-connect/token
)
```

Extract Access Token
```bash
echo "Extract Access Token"
KC_ACCESS_TOKEN=$(echo $KC_RESPONSE | jq -r .access_token)
echo "using KC_ACCESS_TOKEN=$KC_ACCESS_TOKEN"
```

Call greetings API endpoint with Access Token
```bash
echo "Call greetings API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:8090/api/greetings/me
echo
```

Call admin API endpoint with Access Token
```bash
echo "Call admin API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:8090/api/admin
```

Request new Access Token for admin
```bash
echo "Request new Access Token for admin"
KC_RESPONSE=$( \
    curl \
     -s \
     -d "grant_type=password" \
     -d "username=admin" \
     -d "password=admin" \
     -d "client_id=training-tests" \
     -d "client_secret=secret" \
     -d "scope=profile training" \
      $KC_ISSUER/protocol/openid-connect/token
)
```

Extract Access Token
```bash
echo "Extract Access Token"
KC_ACCESS_TOKEN=$(echo $KC_RESPONSE | jq -r .access_token)
echo "using KC_ACCESS_TOKEN=$KC_ACCESS_TOKEN"
```

Call greetings API endpoint with Access Token
```bash
echo "Call greetings API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:8090/api/greetings/me
echo
```

Call admin API endpoint with Access Token
```bash
echo "Call admin API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:8090/api/admin
```