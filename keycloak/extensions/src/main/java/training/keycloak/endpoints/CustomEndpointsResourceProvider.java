package training.keycloak.endpoints;

import com.google.auto.service.AutoService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import java.util.Map;

@RequiredArgsConstructor
public class CustomEndpointsResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    @Override
    public Object getResource() {
        return this;
    }

    /**
     * curl -k -s  http://localhost:9090/auth/realms/training/custom-endpoints/ping | jq -C .
     */
    @Path("/ping")
    public PingResource ping() {
        return new PingResource(session);
    }

    /**
     * curl -k -s  http://localhost:9090/auth/realms/training/custom-endpoints/info | jq -C .
     */
    @Path("/info")
    public InfoResource info() {
        return new InfoResource(session);
    }


    @Override
    public void close() {
        // NOOP
    }

    @JBossLog
    @AutoService(RealmResourceProviderFactory.class)
    public static class Factory implements RealmResourceProviderFactory, ServerInfoAwareProviderFactory {

        public static final String PROVIDER_ID = "custom-endpoints";

        @Override
        public String getId() {
            return PROVIDER_ID;
        }

        @Override
        public RealmResourceProvider create(KeycloakSession session) {
            return new CustomEndpointsResourceProvider(session);
        }

        @Override
        public void init(Config.Scope config) {
            log.info("configure");
        }

        @Override
        public void postInit(KeycloakSessionFactory factory) {
            log.info("Initialize");
        }

        @Override
        public void close() {
            // NOOP
        }

        @Override
        public Map<String, String> getOperationalInfo() {
            return Map.of("ping", "/%s/ping".formatted(PROVIDER_ID), "info", "/%s/info".formatted(PROVIDER_ID));
        }
    }
}
