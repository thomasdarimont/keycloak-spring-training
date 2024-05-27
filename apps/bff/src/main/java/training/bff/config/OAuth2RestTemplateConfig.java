package training.bff.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.client.RestTemplate;
import training.bff.oauth.TokenAccessor;

@Configuration
class OAuth2RestTemplateConfig {

    /**
     * Provides a {@link RestTemplate} that can obtain access tokes for the current user.
     *
     * @param tokenAccessor
     * @return
     */
    @Bean
    @Qualifier("oauth")
    public RestTemplate oauthRestTemplate(TokenAccessor tokenAccessor) {

        var restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {

            var accessToken = tokenAccessor.getAccessTokenForCurrentUser();
            if (accessToken == null) {
                throw new OAuth2AuthenticationException("missing access token");
            }

            var accessTokenValue = accessToken.getTokenValue();
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue);

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
