package training.bff.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

/**
 * Provides access to OAuth2 access- and refresh-tokens of an authenticated user.
 */
@Component
public class TokenAccessor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public TokenAccessor(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public OAuth2AccessToken getAccessTokenForCurrentUser() {
        return getAccessToken(SecurityContextHolder.getContext().getAuthentication());
    }

    public OAuth2AccessToken getAccessToken(Authentication auth) {

        if (!(auth instanceof OAuth2AuthenticationToken authToken)) {
            return null;
        }

        OAuth2AuthorizedClient authorizedClient = getCurrentAuthorizedClient(authToken);
        return authorizedClient.getAccessToken();
    }

    public OAuth2AuthorizedClient getCurrentAuthorizedClient(OAuth2AuthenticationToken authToken) {

        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest //
                .withClientRegistrationId(authToken.getAuthorizedClientRegistrationId()) //
                .principal(authToken.getName()) //
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(oAuth2AuthorizeRequest);
        return authorizedClient;
    }

}
