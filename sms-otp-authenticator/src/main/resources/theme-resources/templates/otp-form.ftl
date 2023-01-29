<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('totp'); section>
    <#if section = "title">
        ${msg("otp")}
    <#elseif section = "header">
        ${msg("otp")}
    <#elseif section = "form">
        <form id="kc-form-send-otp"
              class="box"
              action="${url.loginAction}" method="post">

            <div class="content ${properties.kcFormGroupClass!}">
                <p>${msg("otpSentTo",(maskedPhoneNumber!''))}</p>
            </div>

            <div class="field ${properties.kcFormGroupClass!}">
                <label for="otp-input" class="label ${properties.kcLabelClass!}">${msg("otp")}</label>

                <div class="control">
                    <input tabindex="2" id="otp-code-input" class="input ${properties.kcInputClass!}" name="otp" type="text" autocomplete="off"
                           aria-invalid="<#if messagesPerField.existsError('totp')>true</#if>"
                    />
                </div>
                <#if messagesPerField.existsError('totp')>
                    <p id="input-error-otp-code" class="help is-danger ${properties.kcInputErrorMessageClass!}"
                          aria-live="polite">
                        ${kcSanitize(messagesPerField.get('totp'))?no_esc}
                    </p>
                </#if>

            </div>

            <div id="kc-form-buttons" class="field ${properties.kcFormGroupClass!}">
                <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                <div class="control">
                    <input tabindex="4" class="button is-primary ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="login" id="kc-login" type="submit"
                           value="${msg("doValidateOtp")}"/>
                    <input tabindex="5" class="button ${properties.kcButtonClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="cancel" id="kc-login" type="submit"
                           value="${msg("doCancel")}"/>
                    <input id="kc-resend"
                            name="resend" type="hidden" value="false" />
                </div>

            </div>

            <div id="help-didnt-receive" class="notification" style="display:none">
                ${msg("didntReceive")} <a id="" href="${resendLink}">${msg("doResend")}</a>.
            </div>
        </form>


        <script type="text/javascript">
            setTimeout(() => {
                document.getElementById("help-didnt-receive").style.display = "block";
            }, 10000);
        </script>

    </#if>
</@layout.registrationLayout>
