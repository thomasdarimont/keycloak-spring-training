package training.keycloak.labs.events;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
public class CustomLabsEventListener implements EventListenerProvider {

    public static final String ID = "lab-custom-listener";

    private final KeycloakSession session;

    public CustomLabsEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        log.infof("#### Custom user event %s", event.getType());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        log.infof("#### Custom admin event %s %s", event.getOperationType(), event.getResourcePath());
    }

    @Override
    public void close() {
        // NOOP
    }

    //LABS: Use the AutoService annotation processor to generate the Service-Manifest
//    @AutoService(EventListenerProviderFactory.class)
    public static class Factory
    //
        //LABS: Implement the following interface
//            implements EventListenerProviderFactory
    {

//        @Override
        public String getId() {
            return ID;
        }

//        @Override
        public EventListenerProvider create(KeycloakSession session) {

            //LABS: return the CustomLabsEventListener
            return null;
//            return new CustomLabsEventListener(session);
        }

//        @Override
        public void init(Config.Scope config) {
            // NOOP
        }

//        @Override
        public void postInit(KeycloakSessionFactory factory) {
            // NOOP
        }

//        @Override
        public void close() {
            // NOOP
        }
    }
}
