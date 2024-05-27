package training.webapp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Configuration
class KeycloakRestClientConfig {

    @Bean
    @Qualifier("keycloakRestClient")
    public RestClient keycloakRestClient(OAuth2AuthorizedClientManager authorizedClientManager, ClientRegistrationRepository clientRegistrations) {

        var oauthRequestInterceptor = new OAuth2ClientInterceptor(authorizedClientManager, clientRegistrations.findByRegistrationId("keycloak"));

        return RestClient.builder() //
                .requestInterceptor(oauthRequestInterceptor) //
                .defaultHeaders(headers -> {
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                }) //
                .build();
    }

    public static class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor, ClientHttpRequestInitializer {

        private final OAuth2AuthorizedClientManager manager;
        private final ClientRegistration clientRegistration;

        public OAuth2ClientInterceptor(OAuth2AuthorizedClientManager manager, ClientRegistration clientRegistration) {
            this.manager = manager;
            this.clientRegistration = clientRegistration;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            String accessToken = getBearerToken();
            request.getHeaders().setBearerAuth(accessToken);
            return execution.execute(request, body);
        }

        @Override
        public void initialize(ClientHttpRequest request) {
            request.getHeaders().setBearerAuth(getBearerToken());
        }

        private String getBearerToken() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest //
                    .withClientRegistrationId(clientRegistration.getRegistrationId()) //
                    .principal(authentication.getName()) //
                    .build();

            OAuth2AuthorizedClient authorizedClient = manager.authorize(oAuth2AuthorizeRequest);
            return authorizedClient.getAccessToken().getTokenValue();
        }
    }

}
