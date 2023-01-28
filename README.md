# Keycloak Playground

Collections of SPI, providers, themes and misc examples on how to extends [Keycloak](https://www.keycloak.org/).
This repo is tested in Keycloak ver`20.0.2`.

## Build

### All Examples

`./mvnw clean install -nsu -e -DskipTests`

### Build Selected Modules Only

`./mvnw clean package -DskipTests -pl sms-otp-authenticator`
