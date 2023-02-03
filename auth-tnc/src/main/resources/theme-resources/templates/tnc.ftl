<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('totp'); section>
    <#if section = "title">
        ${msg("tnc")}
    <#elseif section = "header">
        ${msg("tnc")}
    <#elseif section = "form">
        <form id="kc-form-send-otp"
              class="box"
              action="${url.loginAction}" method="post">

            <div class="content ${properties.kcFormGroupClass!}">
                <p>A long paragraph of TnC</p>

            </div>

            <div id="kc-form-buttons" class="field ${properties.kcFormGroupClass!}">
                <div class="control">
                    <input tabindex="4" class="button is-primary ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="accept" id="kc-accept" type="submit"
                           value="${msg("accept")}"/>
                    <input tabindex="5" class="button ${properties.kcButtonClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           name="reject" id="kc-reject" type="submit"
                           value="${msg("reject")}"/>
                </div>

            </div>

        </form>

    </#if>
</@layout.registrationLayout>
