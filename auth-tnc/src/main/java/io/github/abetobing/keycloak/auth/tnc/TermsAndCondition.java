package io.github.abetobing.keycloak.auth.tnc;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.Constants;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@JBossLog
public class TermsAndCondition implements RequiredActionProvider {

    public static final String PROVIDER_ID = "terms-and-condition-auth-page";
    public static final TermsAndCondition SINGLETON;

    static {
        SINGLETON = new TermsAndCondition();
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        writeLog(context);
        AuthenticationSessionModel authSession = context.getAuthenticationSession();


            if (authSession.getClientNotes().containsKey(OAuth2Constants.SCOPE)) {
                ClientModel client = authSession.getClient();
                String scopeParam = authSession.getClientNote(OAuth2Constants.SCOPE);

                Set<String> requestedClientScopes = TokenManager.getRequestedClientScopes(scopeParam, client)
                        .map(ClientScopeModel::getId).collect(Collectors.toSet());

                requestedClientScopes.stream()
                        .filter(s -> s.equals("email"))
                        .findFirst()
                        .ifPresent(s -> authSession.addRequiredAction(PROVIDER_ID));
            }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        writeLog(context);
        context.challenge(generateTncForm(context, null, null));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        writeLog(context);
        MultivaluedMap<String, String> form = context.getHttpRequest().getDecodedFormParameters();

        if (form.containsKey("reject")) {
            cleanSession(context, RequiredActionContext.KcActionStatus.CANCELLED);
            context.failure();
        } else if (form.containsKey("accept")) {
            context.success();
            cleanSession(context, RequiredActionContext.KcActionStatus.SUCCESS);
        }
    }

    protected Response generateTncForm(RequiredActionContext context, String error, String field) {
        LoginFormsProvider form = context.form();
        if (error != null) {
            if (field != null) {
                form.addError(new FormMessage(field, error));
            } else {
                form.setError(error);
            }
        }

        return form.createForm("tnc.ftl");

    }

    private void writeLog(RequiredActionContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        log.infof("%s = %s", Constants.KC_ACTION, authSession.getClientNote(Constants.KC_ACTION));
        log.infof("%s = %s", Constants.KC_ACTION_STATUS, authSession.getClientNote(Constants.KC_ACTION_STATUS));
        log.infof("%s = %s", Constants.KC_ACTION_EXECUTING, authSession.getClientNote(Constants.KC_ACTION_EXECUTING));
        log.infof("%s = %s", AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION, context.getAuthenticationSession().getAuthNote(AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION));
    }
    private void cleanSession(RequiredActionContext context, RequiredActionContext.KcActionStatus status) {
        context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);
        context.getAuthenticationSession().removeAuthNote(AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION);
        AuthenticationManager.setKcActionStatus(PROVIDER_ID, status, context.getAuthenticationSession());
        writeLog(context);
    }

    @Override
    public void close() {

    }
}
