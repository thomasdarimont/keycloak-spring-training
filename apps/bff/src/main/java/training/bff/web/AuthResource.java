package training.bff.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import training.bff.oauth.TokenIntrospector;

@RestController
@RequestMapping("/auth")
class AuthResource {

    private final TokenIntrospector tokenIntrospector;

    public AuthResource(TokenIntrospector tokenIntrospector) {
        this.tokenIntrospector = tokenIntrospector;
    }

    @PostMapping("/check-session")
    public ResponseEntity<?> checkSession(Authentication auth, HttpServletRequest request) throws Exception {

        var introspectionResult = tokenIntrospector.introspectToken(auth);

        if (introspectionResult == null || !introspectionResult.active()) {

            if (auth instanceof OAuth2AuthenticationToken token) {
                request.logout();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }
}
