package training.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import training.webapp.support.HttpSessionOAuth2AuthorizedClientService;
import training.webapp.support.keycloak.KeycloakLogoutHandler;
import training.webapp.support.keycloak.KeycloakRolesMapper;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChainOidc(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        http.authorizeHttpRequests(requests -> {
            requests.requestMatchers("/", "/webjars/**", "/resources/**", "/css/**", "/error").permitAll();
            requests.requestMatchers("/secure", "/secure/**").authenticated();
        });

        // We follow the recommendations of OAuth 2.1 and use PKCE also for confidential clients with auth code grant flow
        http.oauth2Client(o2cc -> {
            var oauth2AuthRequestResolver = new DefaultOAuth2AuthorizationRequestResolver( //
                    clientRegistrationRepository, //
                    OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI //
            );
            oauth2AuthRequestResolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
            o2cc.authorizationCodeGrant(custom -> {
                custom.authorizationRequestResolver(oauth2AuthRequestResolver);
            });
        });

        http.oauth2Login(o2login -> {
            o2login.userInfoEndpoint(customizer -> {
                customizer.userAuthoritiesMapper(new KeycloakRolesMapper());
            });
        });

//        http.oidcLogout(Customizer.withDefaults());

        http.logout(logout -> {
            logout.addLogoutHandler(new KeycloakLogoutHandler());
        });

        return http.build();
    }


    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(OAuth2AuthorizedClientRepository clientRegistrationRepository) {
        return new HttpSessionOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
