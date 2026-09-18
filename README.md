<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-jaxrs.png" width="300" />
</p>


The `jax-rs-pac4j` project is an **easy and powerful security library for JAX-RS web applications and web services** which supports authentication and authorization, but also logout and advanced features like session fixation and CSRF protection.
It's based on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

| jax-rs-pac4j | JDK | pac4j | JAX-RS | Servlet |
|--------------|-----|-------|--------|---------|
| version >= 8 | 17  | v6    | v3 / v4 | v5 / v6 / v6.1 (depending on the module) |
| version >= 7 | 17  | v6    | v3 / v4 | v5      |
| version >= 6 | 11  | v5    | v3     | v5      |
| version >= 5 | 11  | v5    | v2     | v4      |
| version >= 4 | 8   | v4    | v2     | v4      |

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OpenID Connect - SAML - CAS - OAuth - HTTP - LDAP - SQL - JWT - MongoDB - Kerberos - IP address - REST API

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles - Anonymous / remember-me / (fully) authenticated - Profile type, attribute - CORS - CSRF - Security headers - IP address, HTTP method

3) A [**matcher**](http://www.pac4j.org/docs/matchers.html) defines whether the `SecurityFilter` must be applied and can be used for additional web processing

4) Filters protect resources and map some of them to login processes.

- The `SecurityFilter` protects a resource by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients
- The `CallbackFilter` finishes the login process for an indirect client
- The `LogoutFilter` logs out the user from the application.

These filters can be directly registered by hand, or instead, the following features can be used.

5) Generic JAX-RS Providers and Features activate the use of some of the filters on the JAX-RS implementation based on various conditions

- The `Pac4JJaxRsFeature` enables generic JAX-RS based pac4j functionality. The default configuration does not provide session handling (i.e., it will only work with direct clients). The feature registers the following default providers:
    - `JaxRsContextFactoryProvider` to create the generic pac4j context for JAX-RS
    - `JaxRsConfigProvider` to provide the pac4j configuration
    - `JaxRsSessionStoreProvider` to provide the configured pac4j `SessionStore`


- The `Pac4JSecurityFeature` enables annotation-based activation of the filters at the resource method level
- The `Pac4JSecurityFilterFeature` activates a global filter that will be applied to every resource.

6) Container/Implementation-specific Providers and Features extend the basic functionality provided by the generic ones

- The `Pac4JValueFactoryProvider` (Jersey) and the `Pac4JProfileInjectorFactory` (RESTEasy) enable injection of the security profile in resource method

- The `Pac4JServletFeature` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` with `ServletJaxRsContextFactoryProvider` (for Servlet-based JAX-RS implementations, e.g., Jersey on Netty or Grizzly Servlet, Resteasy on Undertow) and `JaxRsSessionStoreProvider` with `ServletSessionStoreProvider`.

- The `Pac4JGrizzlyFeature` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` with `GrizzlyJaxRsContextFactoryProvider` (for Grizzly2 without Servlet support) and `JaxRsSessionStoreProvider` with `GrizzlySessionStoreProvider`.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/jax-rs-pac4j/wiki/Dependencies)

Starting with version 8, choose the integration matching your JAX-RS implementation:

| Maven artifact (`org.pac4j`) | Implementation | Jakarta REST |
|-----------------------------|----------------|--------------|
| `jersey3-pac4j` | Jersey 3.1 | 3.1 |
| `jersey4-pac4j` | Jersey 4.0 | 4.0 |
| `resteasy6-pac4j` | RESTEasy 6.2 | 3.1 |
| `resteasy7-pac4j` | RESTEasy 7.0 | 4.0 |

The new modules share the existing `org.pac4j.jax-rs:core` and retain the integration's
Java package names. Use only one Jersey integration, or one RESTEasy integration,
on an application's classpath; do not combine both generations of the same integration.

For example, to use Jersey 4:

```xml
<dependency>
  <groupId>org.pac4j</groupId>
  <artifactId>jersey4-pac4j</artifactId>
  <version>8.0.0-SNAPSHOT</version>
</dependency>
```

For RESTEasy 7, use `resteasy7-pac4j` instead. The application or server supplies the
matching Jersey or RESTEasy runtime. The Servlet integration tests use Servlet 6.1
for Jersey 4. The RESTEasy test runtimes are aligned separately:

| Module | RESTEasy | Servlet | CDI / Weld | Undertow |
|--------|----------|---------|------------|----------|
| `resteasy6-pac4j` | 6.2.19.Final | 6.0 | 4.0 / 5.1.7.Final | 2.3.26.Final |
| `resteasy7-pac4j` | 7.0.5.Final | 6.1 | 4.1 / 6.0.4.Final | Core 2.4.3.Final + EE 2.0.2.Final |

RESTEasy 7 tests use `io.undertow.ee:undertow-servlet` and exclude the legacy
`io.undertow:undertow-servlet` dependency supplied by `resteasy-undertow`.
Renovate groups these runtime updates per module and allows patch updates within
the listed version families. Moving to another family requires updating the
corresponding stack and its Renovate rules together; CDI 5 coordinate replacements
are excluded until a compatible CDI implementation is adopted.

With RESTEasy 6.2 or 7 and CDI, register `Pac4JSecurityFeature.class` in
`Application.getClasses()` so that CDI constructs the feature and injects its JAX-RS
context. Keep the configured `Pac4JServletFeature` instance in `getSingletons()`;
see the [RESTEasy 7 test application](resteasy7/src/test/java/org/pac4j/jax/rs/rules/RestEasyUndertowServletRule.java)
for a complete example.

### 2) Define:

#### - the [security configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Security-configuration)
#### - the [callback configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Callback-configuration), only for web applications
#### - the [logout configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Logout-configuration)

### 3) [Apply security](https://github.com/pac4j/jax-rs-pac4j/wiki/Apply-security)

### 4) [Get the authenticated user profiles](https://github.com/pac4j/jax-rs-pac4j/wiki/Get-the-authenticated-user-profiles)


## Versions

The latest released version is the [![Maven Central](https://img.shields.io/maven-central/v/org.pac4j/jersey3-pac4j.svg)](https://repo1.maven.org/maven2/org/pac4j/jersey3-pac4j).
The [next version](https://github.com/pac4j/jax-rs-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/jax-rs-pac4j/wiki/Release-Notes).

See the [migration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Migration-guide) as well.


## Demo

A demo: [jax-rs-pac4j-demo](https://github.com/pac4j/jax-rs-pac4j-demo) is available for tests and implement several authentication mechanisms: basic auth, login form and CAS.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
