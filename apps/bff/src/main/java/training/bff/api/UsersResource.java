package training.bff.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
class UsersResource {

    private final RestTemplate oauthRestTemplate;

    public UsersResource(@Qualifier("oauth") RestTemplate oauthRestTemplate) {
        this.oauthRestTemplate = oauthRestTemplate;
    }

    @GetMapping("/me/claims")
    public ResponseEntity<Object> claims(Authentication auth) {
        var claims = ((OidcUser) auth.getPrincipal()).getClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/me/userinfo")
    public ResponseEntity<Object> userInfo(Authentication auth) {
        var principal = (DefaultOidcUser) auth.getPrincipal();
        var idToken = principal.getIdToken();
        var issuerUri = idToken.getIssuer().toString();
        var userInfo = oauthRestTemplate.getForObject(issuerUri + "/protocol/openid-connect/userinfo", Map.class);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/me/remote")
    public ResponseEntity<Object> remoteUserInfo() {
        var data = (Map<String, Object>) oauthRestTemplate.getForObject("http://localhost:8090/api/greetings/me", Map.class);
        return ResponseEntity.ok(data);
    }
}
