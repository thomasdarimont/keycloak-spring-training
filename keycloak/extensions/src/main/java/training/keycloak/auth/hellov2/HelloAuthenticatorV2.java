package training.keycloak.auth.hellov2;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.List;
import java.util.Map;

@JBossLog
public class HelloAuthenticatorV2 implements Authenticator {

    private static final HelloAuthenticatorV2 INSTANCE = new HelloAuthenticatorV2();

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

    @AutoService(AuthenticatorFactory.class)
    public static class Factory implements AuthenticatorFactory, ServerInfoAwareProviderFactory {

        @Override
        public String getId() {
            return "training-auth-hello-v2";
        }

        @Override
        public Authenticator create(KeycloakSession session) {
            return INSTANCE;
        }

        @Override
        public String getDisplayType() {
            return "Training: Hello Authenticator V2";
        }

        @Override
        public String getHelpText() {
            return "Prints a greeting for the user to the console";
        }

        @Override
        public String getReferenceCategory() {
            return "hello";
        }

        @Override
        public boolean isConfigurable() {
            return true;
        }

        @Override
        public List<ProviderConfigProperty> getConfigProperties() {

            List<ProviderConfigProperty> properties = ProviderConfigurationBuilder.create() //
                    .property() //
                    .name("message") //
                    .label("Message") //
                    .helpText("Message text") //
                    .type(ProviderConfigProperty.STRING_TYPE) //
                    .defaultValue("Hello") //
                    .add().build();
            return properties;
        }

        @Override
        public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
            return REQUIREMENT_CHOICES;
        }

        @Override
        public boolean isUserSetupAllowed() {
            return false;
        }

        @Override
        public void postInit(KeycloakSessionFactory factory) {
            // called after factory is found
        }

        @Override
        public void init(Config.Scope config) {

            // spi-authenticator-acme-auth-hello-message
//            config.get("message");
            // called when provider factory is used
        }

        @Override
        public void close() {
            // NOOP
        }

        @Override
        public Map<String, String> getOperationalInfo() {
            String version = getClass().getPackage().getImplementationVersion();
            return Map.of("info", "infoValue", "version", version);
        }
    }
}
