package com.abetobing.keycloak.conditional;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JBossLog
public class ConditionalOnScope implements ConditionalAuthenticator {

    public static final String PROVIDER_ID = "condition-on-scope-exist";
    public static final ConditionalOnScope SINGLETON = new ConditionalOnScope();

    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String scopesString = config.get(ConditionalOnScopeFactory.SELECTED_SCOPES);
        List<String> scopes = Arrays.asList(scopesString.split("\\s+"));

        String currentScopes = context.getAuthenticationSession().getClientNotes().getOrDefault("scope", "");
        return scopes.contains(currentScopes);
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
