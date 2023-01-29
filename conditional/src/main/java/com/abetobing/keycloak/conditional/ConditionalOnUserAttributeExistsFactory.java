package com.abetobing.keycloak.conditional;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class ConditionalOnUserAttributeExistsFactory implements ConditionalAuthenticatorFactory {

    public static final String CONFIG_ATTRIBUTE_NAME = "condition-on-attribute-name";

    @Override
    public String getHelpText() {
        return "Flow is executed only if the user attribute exists";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty mobile = new ProviderConfigProperty();
        mobile.setType(ProviderConfigProperty.STRING_TYPE);
        mobile.setName(CONFIG_ATTRIBUTE_NAME);
        mobile.setLabel("User Attribute");
        mobile.setHelpText("User's attribute name. Example: 'phoneNumber'.");

        return Collections.singletonList(mobile);
    }
    @Override
    public <C> C getConfig() {
        return ConditionalAuthenticatorFactory.super.getConfig();
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return ConditionalOnUserAttributeExists.SINGLETON;
    }

    @Override
    public String getDisplayType() {
        return "Condition - user attribute exists";
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
        return ConditionalOnUserAttributeExists.PROVIDER_ID;
    }
}
