package training.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchange -> {
            exchange.anyExchange().authenticated();
        });
        http.oauth2Login(Customizer.withDefaults());
        http.oauth2ResourceServer(resourceServer -> {
            resourceServer.jwt(Customizer.withDefaults());
        });
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

}
