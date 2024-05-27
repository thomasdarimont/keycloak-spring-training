package training.apiapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.JwkSetUriJwtDecoderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import training.apiapp.support.KeycloakJwtAuthenticationConverter;

@Configuration
@EnableMethodSecurity
class WebSecurityConfig {

    /**
     * Configures basic security handler per HTTP session.
     * <p>
     * <ul>
     * <li>Stateless session (no session kept server-side)</li>
     * <li>JWT converted into Spring token</li>
     * </ul>
     *
     * @param http security configuration
     * @throws Exception any error
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //LABS: Uncomment the following lines to avoid creating sessions
//        http.sessionManagement(sessions -> {
//            sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        });

        http.authorizeHttpRequests(requests -> {
            // declarative route configuration

            //LABS: Uncomment the following lines to protect /api route
//            requests.requestMatchers("/api/**").authenticated();

            // add additional routes
            requests.anyRequest().denyAll(); //
        });


        //LABS: Uncomment the following lines to enable the oauth2 resource server support
//        http.oauth2ResourceServer(resourceServer -> {
//            resourceServer.jwt(Customizer.withDefaults());
//        });

        return http.build();
    }

    //LABS: Uncomment the following lines to add support for keycloak proprietary token claims
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter(OAuth2ResourceServerProperties properties, @Value("training.client-id") String clientId) {
//        return new KeycloakJwtAuthenticationConverter(properties.getJwt().getPrincipalClaimName(), clientId);
//    }

    @Bean
    public JwkSetUriJwtDecoderBuilderCustomizer jwtDecoderBuilderCustomizer() {
        return builder -> {
            System.out.println("Customize JWT Decoder here");
        };
    }

}
