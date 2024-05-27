package training.bff.oauth;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenIntrospector {

    private final TokenAccessor tokenAccessor;

    public TokenIntrospector(TokenAccessor tokenAccessor) {
        this.tokenAccessor = tokenAccessor;
    }

    public IntrospectionResult introspectToken(Authentication auth) {

        if (!(auth instanceof OAuth2AuthenticationToken authToken)) {
            return null;
        }

        var authorizedClient = tokenAccessor.getCurrentAuthorizedClient(authToken);
        if (authorizedClient == null) {
            return null;
        }

        var accessToken = tokenAccessor.getAccessToken(auth);

        // authorized client found, perform token introspection

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", authorizedClient.getClientRegistration().getClientId());
        requestBody.add("client_secret", authorizedClient.getClientRegistration().getClientSecret());
        requestBody.add("token", accessToken.getTokenValue());
        requestBody.add("token_type_hint", "access_token");

        var tokenIntrospectionEndpointUrl = getTokenIntrospectionEndpointUrl(authorizedClient);
        var rt = new RestTemplate();
        var responseEntity = rt.postForEntity(tokenIntrospectionEndpointUrl, new HttpEntity<>(requestBody, headers), IntrospectionResult.class);

        var responseData = responseEntity.getBody();
        if (responseData == null || !responseData.active()) {
            return null;
        }

        return responseData;
    }

    private static String getTokenIntrospectionEndpointUrl(OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getClientRegistration().getProviderDetails().getIssuerUri() + "/protocol/openid-connect/token/introspect";
    }

    public record IntrospectionResult(boolean active, Map<String, Object> data) {

        public IntrospectionResult {
            data = new HashMap<>();
        }

        @JsonAnySetter
        public void setDataEntry(String key, Object value) {
            data.put(key, value);
        }
    }
}
