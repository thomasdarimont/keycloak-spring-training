Spring Cloud Gateway example
---

This example uses Spring Cloud Gateway, which acts as a Oauth Clientand exposes the API provided by the [API](../api) resource server example.

See the [gateway configuration](./src/main/resources/application.yml) for details.

Login via http://localhost:8050
Extract token from http://localhost:8050/token

```bash
echo "Extract Access Token"
KC_ACCESS_TOKEN="INSERT_TOKEN_HERE"
echo "using KC_ACCESS_TOKEN=$KC_ACCESS_TOKEN"
```

Call greetings API endpoint through Gateway with Access Token
```bash
echo "Call greetings API endpoint with Access Token"
curl -s \
     -v \
     -H "Authorization: Bearer $KC_ACCESS_TOKEN" \
     http://localhost:8050/api/greetings/me
echo
```