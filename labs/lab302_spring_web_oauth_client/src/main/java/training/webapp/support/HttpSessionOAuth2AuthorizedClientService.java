package training.webapp.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

public class HttpSessionOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    public HttpSessionOAuth2AuthorizedClientService(OAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authorizedClientRepository.loadAuthorizedClient(clientRegistrationId, authentication, HttpServletRequestUtils.getCurrentHttpServletRequest().get());
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        HttpServletRequest request = HttpServletRequestUtils.getCurrentHttpServletRequest().get();
        HttpServletResponse response = HttpServletRequestUtils.getCurrentHttpServletResponse().get();
        authorizedClientRepository.saveAuthorizedClient(authorizedClient, principal, request, response);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

        HttpServletRequest request = HttpServletRequestUtils.getCurrentHttpServletRequest().get();
        HttpServletResponse response = HttpServletRequestUtils.getCurrentHttpServletResponse().get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication, request, response);
    }
}
