package training.keycloak.auth.hellov1;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class HelloAuthenticatorV1 implements Authenticator {

    private static final Logger log = Logger.getLogger(HelloAuthenticatorV1.class);

    static final HelloAuthenticatorV1 INSTANCE = new HelloAuthenticatorV1();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // authentication entrypoint
        // apply auth logic
        // "force challenge if necessary"
        // else mark as success

        // read current message prefix from authenticator config
        String message = getGreetingMessage(context);

        // extract current username
        String username = context.getAuthenticationSession().getAuthenticatedUser().getUsername();
        log.infof("#### %s %s%n", message, username);

        // add metadata to the login event
        context.getEvent().detail("message", message);

        // mark authenticator execution as success
        context.success();
    }

    private String getGreetingMessage(AuthenticationFlowContext context) {
        AuthenticatorConfigModel authConfig = context.getAuthenticatorConfig();
        String message = authConfig == null ? "Hello" : authConfig.getConfig().getOrDefault("message", "Hello");
        return message;
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // handle user input from FORM submit
        // check input
        // on failure -> force challenge again
        // else mark as success
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

}
