package io.github.abetobing.keycloak.conditional;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class ConditionalOnScopeFactory implements ConditionalAuthenticatorFactory {

    public static final String SELECTED_SCOPES = "condition-on-scopes";

		private static final List<ProviderConfigProperty> providerConfigProperties;

		static {
			ProviderConfigProperty scopes = new ProviderConfigProperty();
			scopes.setType(ProviderConfigProperty.STRING_TYPE);
			scopes.setName(SELECTED_SCOPES);
			scopes.setDefaultValue(null);
			scopes.setLabel("Scope(s)");
			scopes.setHelpText("Allowed scopes separated by space. Example: 'phone email profile'");

			providerConfigProperties = List.of(scopes);
		}

    @Override
    public String getHelpText() {
        return "Flow is executed only if scope exists in current authentication context";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return providerConfigProperties;
    }
    @Override
    public <C> C getConfig() {
        return ConditionalAuthenticatorFactory.super.getConfig();
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return ConditionalOnScope.SINGLETON;
    }

    @Override
    public String getDisplayType() {
        return "Condition - scope";
    }

    @Override
    public boolean isConfigurable() {
        return true;
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
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ConditionalOnScope.PROVIDER_ID;
    }
}
