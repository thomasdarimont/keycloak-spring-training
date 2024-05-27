package training.keycloak.events;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
public class CustomEventListener implements EventListenerProvider {

    public static final String ID = "training-custom-listener";
    private final KeycloakSession session;

    public CustomEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        log.infof("Custom user event %s", event.getType());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        log.infof("Custom admin event %s %s", event.getOperationType(), event.getResourcePath());
    }

    @Override
    public void close() {
        // NOOP
    }

    @AutoService(EventListenerProviderFactory.class)
    public static class Factory implements EventListenerProviderFactory {

        @Override
        public String getId() {
            return ID;
        }

        @Override
        public EventListenerProvider create(KeycloakSession session) {
            return new CustomEventListener(session);
        }

        @Override
        public void init(Config.Scope config) {
            // NOOP
        }

        @Override
        public void postInit(KeycloakSessionFactory factory) {
            // NOOP
        }

        @Override
        public void close() {
            // NOOP
        }
    }
}
