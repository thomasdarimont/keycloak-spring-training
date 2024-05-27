package training.keycloak.auth.hellov1;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

/**
 * See also src/main/resources/META-INF/services/org.keycloak.authentication.AuthenticatorFactory
 */
public class HelloAuthenticatorV1Factory implements AuthenticatorFactory {

    @Override
    public String getId() {
        return "training-auth-hello-v1";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return HelloAuthenticatorV1.INSTANCE;
    }

    @Override
    public String getDisplayType() {
        return "Training: Hello Authenticator V1";
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
}
