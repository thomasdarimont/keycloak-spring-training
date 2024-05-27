package training.cli;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CliApplication {

    private static final Logger log = LoggerFactory.getLogger(CliApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(CliApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    CommandLineRunner clr() {
        return args -> {

            if (!Set.of(args).contains("demo")) {
                return;
            }

            log.info("Running");

            var clientId = "training-cli";
            var scope = "email offline_access";

            var issuerUrl = getIssuerUrl();

            var deviceAuthUrl = issuerUrl + "/protocol/openid-connect/auth/device";
            var tokenUrl = issuerUrl + "/protocol/openid-connect/token";

            log.info("Browse to {} and enter the following code.", deviceAuthUrl);

            var deviceCodeResponseEntity = requestDeviceCode(clientId, scope, deviceAuthUrl);

            log.info("Response code: {}", deviceCodeResponseEntity.getStatusCodeValue());
            var deviceCodeResponse = deviceCodeResponseEntity.getBody();
            log.info("{}", deviceCodeResponse);

            log.info("Browse to {} and enter the code {}", deviceCodeResponse.verification_uri(), deviceCodeResponse.user_code());
            log.info("--- OR ----");
            log.info("Browse to {}", deviceCodeResponse.verification_uri_complete());

            System.out.println("Waiting for completion...");

            var expiresAt = Instant.now().plusSeconds(deviceCodeResponse.expires_in);
            while (Instant.now().isBefore(expiresAt)) {
                log.info("Start device flow");
                try {
                    var deviceFlowResponse = checkForDeviceFlowCompletion(clientId, deviceCodeResponse.device_code(), tokenUrl);
                    log.info("Got response status: {}", deviceFlowResponse.getStatusCode());
                    if (deviceFlowResponse.getStatusCode() == HttpStatus.OK) {
                        log.info("Success!");
                        log.info("{}", deviceFlowResponse.getBody());

                        log.info("We can now use the access token to call APIs!");
                    } else {
                        log.info("Problem!");
                        log.info("{}", deviceFlowResponse.getBody());
                    }
                    break;
                } catch (HttpClientErrorException.BadRequest badRequest) {
                    log.info("Failed ...");
                    log.info("Continue with polling - sleeping...");
                    TimeUnit.SECONDS.sleep(deviceCodeResponse.interval());
                }
            }
        };
    }

    private static String getIssuerUrl() {
        var authServerUrl = "http://localhost:9090/auth";
        var realm = "training";
        var issuerUrl = authServerUrl + "/realms/" + realm;
        return issuerUrl;
    }

    private ResponseEntity<DeviceCodeResponse> requestDeviceCode(String clientId, String scope, String deviceAuthUrl) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", clientId);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
        requestBody.add("scope", scope);

        var rt = new RestTemplate();

        return rt.postForEntity(deviceAuthUrl, new HttpEntity<>(requestBody, headers), DeviceCodeResponse.class);
    }

    private ResponseEntity<AccessTokenResponse> checkForDeviceFlowCompletion(String clientId, String deviceCode, String tokenUrl) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", clientId);
        requestBody.add("device_code", deviceCode);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

        var rt = new RestTemplate();

        return rt.postForEntity(tokenUrl, new HttpEntity<>(requestBody, headers), AccessTokenResponse.class);
    }

    record DeviceCodeResponse(String device_code, String user_code, String verification_uri,
                              String verification_uri_complete, int expires_in, int interval,
                              Map<String, Object> other) {

        DeviceCodeResponse {
            other = new HashMap<>();
        }

        @JsonAnySetter
        public void setValue(String key, Object value) {
            other.put(key, value);
        }
    }

    record AccessTokenResponse(String access_token, String refresh_token, Map<String, Object> other) {

        AccessTokenResponse {
            other = new HashMap<>();
        }

        @JsonAnySetter
        public void setValue(String key, Object value) {
            other.put(key, value);
        }
    }
}
