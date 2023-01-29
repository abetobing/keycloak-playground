package com.abetobing.keycloak.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.events.Errors;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Map;

@JBossLog
@Getter
@Setter
public class OtpAuthenticator extends OTPFormAuthenticator implements Authenticator {

    private static final String MOBILE_NUMBER_ATTRIBUTE = "phoneNumber";
    private static final String OTP_CODE_INPUT_FIELD = "otp";

    private static final String QUERY_STRING_RESEND = "resendOtp";

    private static final String AUTH_NOTE_KEY = "sms-otp-code";

    private static final String RESEND_KEY = "sms-otp-resend-key";
    private String mobileNumber;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // Check if resend code exist and valid
        String key = context.getAuthenticationSession().getAuthNote(RESEND_KEY);
        if (key != null) {
            String requestKey = context.getHttpRequest().getUri().getQueryParameters().getFirst(QUERY_STRING_RESEND);
            if (requestKey != null) {
                if (!requestKey.equals(key)) {
                    context.failure(AuthenticationFlowError.EXPIRED_CODE);
                    return;
                }
            }
        }

        // TODO: randomize otp code
        String otpCode = "777999";
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_KEY, otpCode);
        UserModel user = context.getUser();
        mobileNumber = user.getFirstAttribute(MOBILE_NUMBER_ATTRIBUTE);

        // get attribute from config
        String mobileNumber = user.getFirstAttribute(getConfig(context, OtpAuthenticatorFactory.CONFIG_ATTRIBUTE_NAME));
        if (mobileNumber == null) {
            log.error("User has not defined a phone number");
            context.getEvent().error("user_has_no_phone_number");

            boolean stopIfNull = Boolean.parseBoolean(getConfig(context, OtpAuthenticatorFactory.CONFIG_STOP_IF_NULL));
            if (stopIfNull) {
                // if stop-if-null is true, then current execution will throw exception
                // and authentication will be marked as failed
                throw new AuthenticationFlowException(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, Errors.GENERIC_AUTHENTICATION_ERROR, "User has no mobile phone number");
            } else {
                // if user has no phone number set, then mark this flow as success
                context.success();
                return;
            }
        }


        // TODO: get user's mobile then send OTP
        log.infof("Sending OTP to: %s", mobileNumber);


        Response challengeForm = challenge(context, null, null);
        context.challenge(challengeForm);
    }

    @Override
    protected Response challenge(AuthenticationFlowContext context, String error, String field) {
        LoginFormsProvider form = context.form()
                .setExecution(context.getExecution().getId());
        if (error != null) {
            if (field != null) {
                form.addError(new FormMessage(field, error));
            } else {
                form.setError(error);
            }
        }
        String key = KeycloakModelUtils.generateId();
        context.getAuthenticationSession().setAuthNote(RESEND_KEY, key);
        String resendLink = KeycloakUriBuilder.fromUri(context.getRefreshExecutionUrl()).queryParam(QUERY_STRING_RESEND, key).build().toString();
        form.setAttribute("resendLink", resendLink);

        // mask phone number
        String maskedPhoneNumber = mobileNumber.replaceAll("\\d(?=(?:\\D*\\d){4})", "*");
        form.setAttribute("maskedPhoneNumber", maskedPhoneNumber);

        return createLoginForm(form);

    }

    @Override
    protected Response createLoginForm(LoginFormsProvider form) {
        return form.createForm("otp-form.ftl");
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        validateInput(context);
    }

    private void validateInput(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> form = context.getHttpRequest().getDecodedFormParameters();

        if (form.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }

        // this is just an example, do your own OTP code validation
        String otpInput = context.getHttpRequest().getDecodedFormParameters().getFirst(OTP_CODE_INPUT_FIELD);
        String expectedInput = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_KEY);
        if (!otpInput.equals(expectedInput)) {
            log.warnf("SMS OTP challenge failed '%s'. Expect '%s' but user input '%s'", mobileNumber, expectedInput, otpInput);
            Response challengeForm = challenge(context, Messages.INVALID_TOTP, Validation.FIELD_OTP_CODE);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeForm);
            return;
        }
        context.success();
    }

    private String getConfig(AuthenticationFlowContext context, String configName) {
        AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
        Map<String, String> config = configModel.getConfig();
        return config.get(configName);
    }


    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // NOOP
    }

    @Override
    protected String disabledByBruteForceError() {
        return Messages.INVALID_TOTP;
    }

    @Override
    protected String disabledByBruteForceFieldError() {
        return Validation.FIELD_OTP_CODE;
    }

    @Override
    public void close() {
        // NOOP
    }
}
