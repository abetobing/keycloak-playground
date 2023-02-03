package io.github.abetobing.keycloak.conditional;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Map;

@JBossLog
public class ConditionalOnUserAttributeExists implements ConditionalAuthenticator {

    public static final String PROVIDER_ID = "condition-on-user-attribute-exist";
    public static final ConditionalOnUserAttributeExists SINGLETON = new ConditionalOnUserAttributeExists();

    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String mobileNumber = user.getFirstAttribute(config.get(ConditionalOnUserAttributeExistsFactory.CONFIG_ATTRIBUTE_NAME));
        return (mobileNumber != null);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // no-op
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // no-op
    }

    @Override
    public void close() {

    }
}
