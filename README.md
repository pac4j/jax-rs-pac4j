<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-jaxrs.png" width="300" />
</p>


The `jax-rs-pac4j` project is an **easy and powerful security library for JAX-RS web applications and web services** which supports authentication and authorization, but also logout and advanced features like session fixation and CSRF protection.
It's based on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

| jax-rs-pac4j | JDK | pac4j | JAX-RS | Servlet |
|--------------|-----|-------|--------|---------|
| version >= 7 | 17  | v6    | v3     | v5      |
| version >= 6 | 11  | v5    | v3     | v5      |
| version >= 5 | 11  | v5    | v2     | v4      |
| version >= 4 | 8   | v4    | v2     | v4      |

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - Google App Engine - LDAP - SQL - JWT - MongoDB - CouchDB - Kerberos - IP address - Kerberos (SPNEGO) - REST API

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

- The `Pac4JValueFactoryProvider` enables injection of the security profile in resource method

- The `Pac4JServletFeature` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` with `ServletJaxRsContextFactoryProvider` (for Servlet-based JAX-RS implementations, e.g., Jersey on Netty or Grizzly Servlet, Resteasy on Undertow) and `JaxRsSessionStoreProvider` with `ServletSessionStoreProvider`.

- The `Pac4JGrizzlyFeature` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` with `GrizzlyJaxRsContextFactoryProvider` (for Grizzly2 without Servlet support) and `JaxRsSessionStoreProvider` with `GrizzlySessionStoreProvider`.


## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/jax-rs-pac4j/wiki/Dependencies)

### 2) Define:

#### - the [security configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Security-configuration)
#### - the [callback configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Callback-configuration), only for web applications
#### - the [logout configuration](https://github.com/pac4j/jax-rs-pac4j/wiki/Logout-configuration)

### 3) [Apply security](https://github.com/pac4j/jax-rs-pac4j/wiki/Apply-security)

### 4) [Get the authenticated user profiles](https://github.com/pac4j/jax-rs-pac4j/wiki/Get-the-authenticated-user-profiles)


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j.jax-rs/core/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j.jax-rs/core), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/jax-rs-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/jax-rs-pac4j/wiki/Release-Notes).

See the [migration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Migration-guide) as well.


## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any questions, want to contribute or be notified about the new releases and security fixes, please subscribe to the following [mailing lists](http://www.pac4j.org/mailing-lists.html):

- [pac4j-users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j-developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
- [pac4j-announce](https://groups.google.com/forum/?hl=en#!forum/pac4j-announce)
- [pac4j-security](https://groups.google.com/forum/#!forum/pac4j-security)
