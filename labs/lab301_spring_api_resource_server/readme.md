Lab 301: Secure a Spring Boot Resource Server with Keycloak
---

# Instructions

Search for `//LABS:` Instructions then run the following steps.

# Run

Use the `ApiApplicationLabs` launch configuration or run the following command in a terminal.

```
java -jar target/*.jar
```

# Misc

## curl examples

Request new Access Token for user
```bash
KC_ISSUER="http://localhost:9090/auth/realms/labs"
echo "Request new Access Token for user"
KC_RESPONSE=$( \
    curl \
     -s \
     -d "grant_type=password" \
     -d "username=user" \
     -d "password=user" \
     -d "client_id=labs-tests" \
     -d "client_secret=secret" \
     -d "scope=profile training" \
      $KC_ISSUER/protocol/openid-connect/token
)

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
     http://localhost:18090/api/greetings/me
echo
```

Call admin API endpoint with Access Token
```bash
echo "Call admin API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:18090/api/admin
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
     -d "client_id=labs-tests" \
     -d "client_secret=secret" \
     -d "scope=profile training" \
      $KC_ISSUER/protocol/openid-connect/token
)

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
     http://localhost:18090/api/greetings/me
echo
```

Call admin API endpoint with Access Token
```bash
echo "Call admin API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:18090/api/admin
```