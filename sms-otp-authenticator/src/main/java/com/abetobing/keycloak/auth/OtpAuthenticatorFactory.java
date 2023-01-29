package com.abetobing.keycloak.auth;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

@JBossLog
public class OtpAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    public static final String PROVIDER_ID = "sms-otp-authenticator";
    public static final String TYPE = "SMS OTP authenticator";
    public static final String CONFIG_ATTRIBUTE_NAME = "sms-otp-attribute-name";
    public static final String CONFIG_STOP_IF_NULL = "sms-otp-stop-if-null";
    private static final OtpAuthenticator SINGLETON = new OtpAuthenticator();

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public String getDisplayType() {
        return TYPE;
    }

    @Override
    public String getReferenceCategory() {
        return null;
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
    public String getHelpText() {
        return "Two-Factor authentication using TOTP over SMS.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty mobile = new ProviderConfigProperty();
        mobile.setType(ProviderConfigProperty.STRING_TYPE);
        mobile.setName(CONFIG_ATTRIBUTE_NAME);
        mobile.setDefaultValue("phoneNumber");
        mobile.setLabel("Mobile Number Attribute");
        mobile.setHelpText("Mobile number attribute name. Default: 'phoneNumber'.");

        ProviderConfigProperty stopIfNull = new ProviderConfigProperty();
        stopIfNull.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        stopIfNull.setName(CONFIG_STOP_IF_NULL);
        stopIfNull.setDefaultValue(false);
        stopIfNull.setLabel("Stop if null");
        stopIfNull.setHelpText("Stop authentication if user attribute is null.");

        return Arrays.asList(mobile, stopIfNull);
    }
    @Override
    public <C> C getConfig() {
        return AuthenticatorFactory.super.getConfig();
    }


    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory sessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
