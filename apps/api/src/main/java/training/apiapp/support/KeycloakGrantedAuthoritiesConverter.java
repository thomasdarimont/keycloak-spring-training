package training.apiapp.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final Converter<Jwt, Collection<GrantedAuthority>> JWT_SCOPE_GRANTED_AUTHORITIES_CONVERTER = new JwtGrantedAuthoritiesConverter();

    private final String clientId;

    public KeycloakGrantedAuthoritiesConverter(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = mapKeycloakRolesToAuthorities( //
                getRealmRolesFrom(jwt), //
                getClientRolesFrom(jwt, clientId) //
        );

        Collection<GrantedAuthority> scopeAuthorities = JWT_SCOPE_GRANTED_AUTHORITIES_CONVERTER.convert(jwt);
        if (!CollectionUtils.isEmpty(scopeAuthorities)) {
            authorities.addAll(scopeAuthorities);
        }

        return authorities;
    }

    protected Collection<GrantedAuthority> mapKeycloakRolesToAuthorities(Set<String> realmRoles, Set<String> clientRoles) {

        List<GrantedAuthority> combinedAuthorities = new ArrayList<>();

        combinedAuthorities.addAll(realmRoles.stream() //
                .map(SimpleGrantedAuthority::new) //
                .toList());

        combinedAuthorities.addAll(clientRoles.stream() //
                .map(SimpleGrantedAuthority::new) //
                .toList());

        return combinedAuthorities;
    }

    protected Set<String> getRealmRolesFrom(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (CollectionUtils.isEmpty(realmAccess)) {
            return Collections.emptySet();
        }

        @SuppressWarnings("unchecked")
        Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
        if (CollectionUtils.isEmpty(realmRoles)) {
            return Collections.emptySet();
        }

        return realmRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    protected Set<String> getClientRolesFrom(Jwt jwt, String clientId) {

        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (CollectionUtils.isEmpty(resourceAccess)) {
            return Collections.emptySet();
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> clientAccess = (Map<String, List<String>>) resourceAccess.get(clientId);
        if (CollectionUtils.isEmpty(clientAccess)) {
            return Collections.emptySet();
        }

        List<String> clientRoles = clientAccess.get("roles");
        if (CollectionUtils.isEmpty(clientRoles)) {
            return Collections.emptySet();
        }

        return clientRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    private String normalizeRole(String role) {
        return role.replace('-', '_');
    }
}
