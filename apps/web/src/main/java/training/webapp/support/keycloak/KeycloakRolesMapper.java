package training.webapp.support.keycloak;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeycloakRolesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        authorities.forEach(authority -> {

            if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                OidcIdToken idToken = oidcUserAuthority.getIdToken();

                if (idToken.hasClaim("realm_access")) {
                    // extracts Keycloak specific roles
                    List<String> roleNames = (List<String>) idToken.getClaimAsMap("realm_access").get("roles");
                    List<GrantedAuthority> realmRoles = AuthorityUtils.createAuthorityList(roleNames);
                    mappedAuthorities.addAll(realmRoles);
                }
            }
        });

        return mappedAuthorities;
    }
}
