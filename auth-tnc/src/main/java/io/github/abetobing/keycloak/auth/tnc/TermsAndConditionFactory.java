package io.github.abetobing.keycloak.auth.tnc;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class TermsAndConditionFactory implements RequiredActionFactory {


    @Override
    public String getId() {
        return TermsAndCondition.PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return "Show terms & condition page";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return TermsAndCondition.SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }


    @Override
    public void close() {

    }

}
