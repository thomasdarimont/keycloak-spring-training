package training.keycloak.events;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventListenerTransaction;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
public class AuditEventListener implements EventListenerProvider {

    public static final String ID = "training-audit-listener";

    private final KeycloakSession session;

    private final EventListenerTransaction tx;

    public AuditEventListener(KeycloakSession session) {
        this.session = session;
        this.tx = new EventListenerTransaction(this::processAdminEventAfterTransaction, this::processUserEventAfterTransaction);
        session.getTransactionManager().enlistAfterCompletion(tx);
    }

    @Override
    public void onEvent(Event event) {
        tx.addEvent(event);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRep) {
        tx.addAdminEvent(event, includeRep);
    }

    private void processUserEventAfterTransaction(Event event) {
        // called for each UserEvent’s
        log.infof("Forward to audit service: audit userEvent %s", event.getType());

        try {
            var context = session.getContext();
            var realm = context.getRealm();
            var authSession = context.getAuthenticationSession();
            var user = authSession == null ? null : authSession.getAuthenticatedUser();

            if (user == null) {
                return;
            }

            log.infof("Handle audit event %s", event.getType());
        } catch (Exception ex) {
            log.errorf(ex, "Failed to handle userEvent %s", event.getType());
        }
    }

    private void processAdminEventAfterTransaction(AdminEvent event, boolean includeRep) {
        // called for each AdminEvent’s
        // log.infof("Forward to audit service: audit adminEvent %s", event);
    }

    @Override
    public void close() {
        // called after component use
    }

    @AutoService(EventListenerProviderFactory.class)
    public static class Factory implements EventListenerProviderFactory {

        @Override
        public String getId() {
            return AuditEventListener.ID;
        }

        @Override // return singleton instance, create new AcmeAuditListener(session) or use lazy initialization
        public EventListenerProvider create(KeycloakSession session) {
            return new AuditEventListener(session);
        }

        @Override
        public void init(Config.Scope config) {
            /* configure factory */
        }

        @Override // we could init our provider with information from other providers
        public void postInit(KeycloakSessionFactory factory) { /* post-process factory */ }

        @Override // close resources if necessary
        public void close() { /* release resources if necessary */ }
    }

}
