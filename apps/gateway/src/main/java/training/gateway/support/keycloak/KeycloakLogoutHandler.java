package training.gateway.support.keycloak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

// TODO fixme
@Component
public class KeycloakLogoutHandler implements ServerLogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(KeycloakLogoutHandler.class);

    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    private String generateAppUri(ServerWebExchange exchange) {
        URI requestUri = exchange.getRequest().getURI();
        var hostname = requestUri.getHost() + ":" + requestUri.getPort();
        var isStandardHttps = "https".equals(requestUri.getScheme()) && requestUri.getPort() == 443;
        var isStandardHttp = "http".equals(requestUri.getScheme()) && requestUri.getPort() == 80;
        if (isStandardHttps || isStandardHttp) {
            hostname = requestUri.getHost();
        }
        return requestUri.getScheme() + "://" + hostname + requestUri.getPath();
    }

    private String createKeycloakLogoutUrl(String issuerUri, String clientId, String idTokenValue, String defaultRedirectUri) {
        return issuerUri + "/protocol/openid-connect/logout?client_id=" + clientId + "&id_token_hint=" + idTokenValue + "&post_logout_redirect_uri=" + defaultRedirectUri;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange filterExchange, Authentication auth) {
        var principal = (DefaultOidcUser) auth.getPrincipal();
        var idToken = principal.getIdToken();

        log.info("Propagate logout to keycloak for user. userId={}", idToken.getSubject());

        var issuerUri = idToken.getIssuer().toString();
        var idTokenValue = idToken.getTokenValue();

        ServerWebExchange serverWebExchange = filterExchange.getExchange();
        var defaultRedirectUri = generateAppUri(serverWebExchange);

        String logoutUrl = createKeycloakLogoutUrl(issuerUri, idToken.getAuthorizedParty(), idTokenValue, defaultRedirectUri);
        // Redirect to Keycloak logout URL using ServerRedirectStrategy
        return Mono.defer(() -> {
            URI redirectUri = URI.create(logoutUrl);
            return redirectStrategy.sendRedirect(serverWebExchange, redirectUri);
        });
    }
}
