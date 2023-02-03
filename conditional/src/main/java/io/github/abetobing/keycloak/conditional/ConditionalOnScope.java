package io.github.abetobing.keycloak.conditional;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Arrays;
import java.util.Collections;
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
        List<String> allowedScopes = Arrays.asList(scopesString.split("\\s+"));

        scopesString = context.getAuthenticationSession().getClientNotes().getOrDefault("scope", "");
        List<String> currentScopes = Arrays.asList(scopesString.split("\\s+"));

        return !Collections.disjoint(currentScopes, allowedScopes);
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
