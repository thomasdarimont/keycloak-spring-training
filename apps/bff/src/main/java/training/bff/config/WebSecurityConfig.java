package training.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import training.bff.support.keycloak.KeycloakLogoutHandler;
import training.bff.support.keycloak.KeycloakRolesMapper;

@Configuration
class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, //
                                           OAuth2AuthorizedClientService oAuth2AuthorizedClientService, //
                                           ClientRegistrationRepository clientRegistrationRepository, //
                                           AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository, //
                                           OAuth2AuthorizedClientRepository oauth2AuthorizedClientRepository, KeycloakLogoutHandler keycloakLogoutHandler //
    ) throws Exception {

        http.csrf(csrf -> {
            csrf.ignoringRequestMatchers("/app/**", "/auth/check-session") //
                    .csrfTokenRepository(new CookieCsrfTokenRepository());
        });

        http.authorizeHttpRequests(requests -> {
            // declarative route configuration
            // add additional routes
            requests.requestMatchers("/app/**", "/webjars/**", "/resources/**", "/css/**").permitAll();
            requests.anyRequest().authenticated();
        });

        http.sessionManagement(sessionManagement -> {
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        });

        http.exceptionHandling(exceptions -> {
            exceptions.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**"));
        });

        // for the sake of the example disable frame protection
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // by default spring security oauth2 client does not support PKCE for confidential clients for auth code grant flow,
        // we explicitly enable the PKCE customization here.
        http.oauth2Client(oauthClient -> {
            var oauth2AuthRequestResolver = new DefaultOAuth2AuthorizationRequestResolver( //
                    clientRegistrationRepository, //
                    OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI //
            );
            oauth2AuthRequestResolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());

            oauthClient.clientRegistrationRepository(clientRegistrationRepository);
            oauthClient.authorizedClientService(oAuth2AuthorizedClientService);
            oauthClient.authorizationCodeGrant(acgc -> {
                acgc.authorizationRequestResolver(oauth2AuthRequestResolver) //
                        .authorizationRequestRepository(authorizationRequestRepository);
            });

        });
        http.oauth2Login(oauthLogin -> {
            oauthLogin.authorizedClientRepository(oauth2AuthorizedClientRepository);
            oauthLogin.userInfoEndpoint(customizer -> {
                customizer.userAuthoritiesMapper(new KeycloakRolesMapper());
            });
        });

        http.logout(logout -> {
            logout.addLogoutHandler(keycloakLogoutHandler);
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
}