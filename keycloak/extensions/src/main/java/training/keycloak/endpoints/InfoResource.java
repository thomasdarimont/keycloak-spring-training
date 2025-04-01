package training.keycloak.endpoints;

import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.Map;

@RequiredArgsConstructor
public class InfoResource {

    private final KeycloakSession session;

    @GET
    public Object info() {

        KeycloakContext context = session.getContext();
        RealmModel realm = context.getRealm();

        return Map.of("type", "info", "time", System.currentTimeMillis(), "realm", realm.getName());
    }
}
