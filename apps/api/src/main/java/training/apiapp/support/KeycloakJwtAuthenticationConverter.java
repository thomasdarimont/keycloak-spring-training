package training.apiapp.support;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class KeycloakJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public KeycloakJwtAuthenticationConverter(String principalClaimName) {
        setPrincipalClaimName(principalClaimName);
        setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter("app-api"));
    }
}
