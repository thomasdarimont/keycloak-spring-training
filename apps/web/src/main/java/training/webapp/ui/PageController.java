package training.webapp.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import training.webapp.support.oauth.HasScope;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

@Controller
class PageController {

    @Qualifier("keycloak")
    private final RestClient keycloakRestClient;

    public PageController(RestClient keycloakRestClient) {
        this.keycloakRestClient = keycloakRestClient;
    }

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @HasScope("training") // Support for annotation parameters new in Spring Security 6.3
    @GetMapping("/secure/home")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/secure/userinfo")
    public String userinfo(Model model, Authentication auth) {
        OidcUser oidcUser = (OidcUser) auth.getPrincipal();
        model.addAttribute("userinfo", new TreeMap<>(oidcUser.getUserInfo().getClaims()));
        return "userinfo";
    }

    @GetMapping("/secure/token")
    public String token(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String tokenValue = accessToken.getTokenValue();
        model.addAttribute("accessTokenValue", tokenValue);

        String[] jwtParts = tokenValue.split("\\.");
        model.addAttribute("accessTokenHeaderDecoded", jsonPrettyPrint(new String(Base64.getDecoder().decode(jwtParts[0]))));
        model.addAttribute("accessTokenPayloadDecoded", jsonPrettyPrint(new String(Base64.getDecoder().decode(jwtParts[1]))));
        model.addAttribute("accessTokenSignature", jwtParts[2]);

        return "token";
    }

    @GetMapping("/secure/roles")
    public String roles(Model model, Authentication auth) {
        model.addAttribute("roles", auth.getAuthorities());
        return "roles";
    }

    @GetMapping("/secure/api")
    public String api(Model model) {
        try {
            Map response = keycloakRestClient.get().uri("http://localhost:8090/api/greetings/me").retrieve().body(Map.class);
            model.addAttribute("apiResponse", response);
        } catch (Exception ex) {
            model.addAttribute("apiResponse", Map.of("error", ex.getMessage()));
        }

        return "api";
    }

    @GetMapping("/secure/api/admin")
    public String adminApi(Model model) {
        try {
            Map response = keycloakRestClient.get().uri("http://localhost:8090/api/admin").retrieve().body(Map.class);
            model.addAttribute("apiResponse", response);
        } catch (Exception ex) {
            model.addAttribute("apiResponse", Map.of("error", ex.getMessage()));
        }

        return "admin";
    }

    private static String jsonPrettyPrint(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object o = objectMapper.readValue(json, Object.class);
            String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            return pretty;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
