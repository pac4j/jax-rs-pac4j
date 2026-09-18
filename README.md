<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-jaxrs.png" width="300" />
</p>

**jax-rs-pac4j** secures Jakarta REST applications with authentication, authorization, login callbacks and logout. It uses the [pac4j security engine](https://github.com/pac4j/pac4j) and supports OpenID Connect, SAML, CAS, OAuth, JWT and other authentication mechanisms.

Version **8.0.0** supports **Jersey 3 and 4** and **RESTEasy 6 and 7**, with **Java 17** and **pac4j 6.5.8**. The project is available under the Apache 2 license.

## Get started

Choose the module matching your runtime:

| Runtime | Maven artifact (`org.pac4j`) |
|---|---|
| Jersey 3.1 | `jersey3-pac4j` |
| Jersey 4.0 | `jersey4-pac4j` |
| RESTEasy 6.2 | `resteasy6-pac4j` |
| RESTEasy 7.0 | `resteasy7-pac4j` |

For example, for Jersey 4:

```xml
<dependency>
  <groupId>org.pac4j</groupId>
  <artifactId>jersey4-pac4j</artifactId>
  <version>8.0.0</version>
</dependency>
```

Add the pac4j modules for your authentication mechanisms and supply the matching Jersey or RESTEasy runtime. The [dependency guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Dependencies) explains the supported combinations.

## Documentation

1. [Configure authentication and register the integration](https://github.com/pac4j/jax-rs-pac4j/wiki/Security-configuration), including Servlet, Grizzly, CDI and sessionless setups.
2. [Protect resources](https://github.com/pac4j/jax-rs-pac4j/wiki/Apply-security) with `@Pac4JSecurity`.
3. [Access the authenticated user profile](https://github.com/pac4j/jax-rs-pac4j/wiki/Get-the-authenticated-user-profiles).
4. For browser login, configure the [callback](https://github.com/pac4j/jax-rs-pac4j/wiki/Callback-configuration) and [logout](https://github.com/pac4j/jax-rs-pac4j/wiki/Logout-configuration) endpoints.

See [components and features](https://github.com/pac4j/jax-rs-pac4j/wiki/Components-and-features) for the integration architecture, and the [migration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Migration-guide) when upgrading an existing application.

## Versions

Read the [release notes](https://github.com/pac4j/jax-rs-pac4j/wiki/Release-notes) or see [development builds](https://github.com/pac4j/jax-rs-pac4j/wiki/Next-version).

[![Maven Central](https://img.shields.io/maven-central/v/org.pac4j/jersey3-pac4j.svg)](https://repo.maven.apache.org/maven2/org/pac4j/jersey3-pac4j/)

## Demo

The [jax-rs-pac4j demo](https://github.com/pac4j/jax-rs-pac4j-demo) illustrates authentication with HTTP Basic, forms and CAS.

## Need help?

Use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or [commercial support](https://www.pac4j.org/commercial-support.html).
