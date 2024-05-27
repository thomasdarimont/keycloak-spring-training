package training.apiapp.support;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class KeycloakJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public KeycloakJwtAuthenticationConverter(String principalClaimName, String clientId) {
        setPrincipalClaimName(principalClaimName);
        setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter(clientId));
    }
}
