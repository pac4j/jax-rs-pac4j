<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-jaxrs.png" width="300" />
</p>

The `jax-rs-pac4j` project is an **easy and powerful security library for JAX-RS** web applications which supports authentication and authorization, but also application logout and advanced features like session fixation and CSRF protection.
It's based on Java 8, servlet 3 (when present), JAX-RS 2 and on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - LDAP - SQL - JWT - MongoDB - Stormpath - IP address

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles / permissions - Anonymous / remember-me / (fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) Filters protect resources and map some of them to login processes.

- The `SecurityFilter` protects a resource by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients
- The `CallbackFilter` finishes the login process for an indirect client
- The `ApplicationLogoutFilter` logs out the user from the application.

These filters can be directly registered by hand, or instead, the following features can be used.

4) Generic JAX-RS Providers and Features activate the use of some of the filters on the JAX-RS implementation based on various conditions

- The `JaxRsContextFactoryProvider` enables generic JAX-RS based pac4j functionning, without session handling (i.e., it will only work with direct clients)
- The `Pac4JSecurityFeature` enables annotation-based activation of the filters at the resource method level
- The `Pac4JSecurityFilterFeature` activates a global filter that will be applied to every resources.

5) Container/Implementation-specific Providers and Features extend the basic functionality provided by the generic ones

- The `Pac4JProfileValueFactoryProvider` enables injection of the security profile in resource method (for Apache Jersey)
- The `ServletJaxRsContextFactoryProvider` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` (for Servlet-based JAX-RS implementations, e.g., Jersey on Netty or Grizzly Servlet, Resteasy on Undertow).
- The `GrizzlyJaxRsContextFactoryProvider` provides session handling (and thus indirect clients support) by replacing the generic `JaxRsContextFactoryProvider` (for Grizzly2 without Servlet support).

---

Just follow these easy steps to secure your JAX-RS web application.
See also [dropwizard-pac4j](https://github.com/pac4j/dropwizard-pac4j) for even easier configuration when using [dropwizard](http://www.dropwizard.io)!

### 1) Add the required dependencies (`jax-rs-pac4j` + `pac4j-*` libraries)

You need to add a dependency on:
 
- the `jax-rs-pac4j` library (<em>groupId</em>: **org.pac4j**, *version*: **1.2.1**)
- the appropriate `pac4j` [submodules](http://www.pac4j.org/docs/clients.html) (<em>groupId</em>: **org.pac4j**, *version*: **1.9.6**): `pac4j-oauth` for OAuth support (Facebook, Twitter...), `pac4j-cas` for CAS support, `pac4j-ldap` for LDAP authentication, etc.

All released artifacts are available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j).

---

### 2) Define the configuration (`Config` + `Client` + `Authorizer`)

The configuration (`org.pac4j.core.config.Config`) contains all the clients and authorizers required by the application to handle security.


```java
GoogleOidcClient oidcClient = new GoogleOidcClient();
oidcClient.setClientID("id");
oidcClient.setSecret("secret");
oidcClient.addCustomParam("prompt", "consent");

SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks",
    "pac4j-demo-passwd", "pac4j-demo-passwd", "resource:testshib-providers.xml");
cfg.setMaximumAuthenticationLifetime(3600);
cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
cfg.setServiceProviderMetadataPath("sp-metadata.xml");
SAML2Client saml2Client = new SAML2Client(cfg);

FacebookClient facebookClient = new FacebookClient("fbId", "fbSecret");
TwitterClient twitterClient = new TwitterClient("twId", "twSecret");

FormClient formClient = new FormClient("http://localhost:8080/loginForm.jsp",
    new SimpleTestUsernamePasswordAuthenticator());
IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(
    new SimpleTestUsernamePasswordAuthenticator());

CasClient casClient = new CasClient("http://mycasserver/login");

ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator("salt"));

Config config = new Config("/callback", oidcClient, saml2Client, facebookClient,
	                  twitterClient, formClient, basicAuthClient, casClient, parameterClient);
config.getClients().setCallbackUrlResolver(new JaxRsCallbackUrlResolver());

config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
config.addAuthorizer("custom", new CustomAuthorizer());
}
```

#### Customization

1) **RECOMMENDED** the `JaxRsCallbackUrlResolver` as the default callback url resolver, it will ensure that in practice, the callback url passed to external authentication system corresponds to the real URL of the callback endpoint

2) a specific [`SessionStore`](http://www.pac4j.org/docs/session-store.html) using the `setSessionStore(sessionStore)` method (by default, with `JaxRsContextFactoryProvider`, session handling is not supported; with `ServletJaxRsContextFactoryProvider`, it uses the `ServletJaxRsSessionStore` which relies on the underlying Servlet Container HTTP session; and with `GrizzlyJaxRsContextFactoryProvider`, it uses the `GrizzlySessionStore` which relies on the underlying HTTP session managed by Grizzly).

3) specific [matchers](http://www.pac4j.org/docs/matchers.html) via the `addMatcher(name, Matcher)` method.


#### JAX-RS Configuration

The configuration is then passed to the various Providers and Features presented previously.

For a bare JAX-RS implementation without session management and annotation-support (here with Jersey, to be adapted):
```java
resourceConfig
    .register(new JaxRsContextFactoryProvider(config))
    .register(new Pac4JSecurityFeature(config));
```

For a Jersey-based and Servlet-based (e.g., Jetty or Grizzly Servlet) environment with session management, annotation support and method parameters injection:
```java
resourceConfig
    .register(new ServletJaxRsContextFactoryProvider(config))
    .register(new Pac4JSecurityFeature(config))
    .register(new Pac4JValueFactoryProvider.Binder());
```

For a Jersey-based and Grizzly-based environment without Servlet but session management and annotation support and method parameters injection:
```
resourceConfig
    .register(new GrizzlyJaxRsContextFactoryProvider(config))
    .register(new Pac4JSecurityFeature(config))
    .register(new Pac4JValueFactoryProvider.Binder());
```

For a Resteasy-based and Servlet-based (e.g., Undertow) environment with session management and annotation support:
```java
    public class MyApp extends Application {
        ...

        @Override
        public Set<Object> getSingletons() {
            Config config = getConfig();
            Set<Object> singletons = new HashSet<>();
            singletons.add(new ServletJaxRsContextFactoryProvider(config));
            singletons.add(new Pac4JSecurityFeature(config));
            return singletons;
        }
    }
```

Note that a default value for the `clients` parameter of the `@Pac4JSecurity`
annotation can be passed to the constructor of `Pac4JSecurityFeature`.

---

### 3) Protect urls (`SecurityFilter`)

You can protect (authentication + authorizations) the urls of your JAX-RS application by using the `SecurityFilter` and defining the appropriate mapping. It has the following behaviour:

1) If the HTTP request matches the `matchers` configuration (or no `matchers` are defined), the security is applied. Otherwise, the user is automatically granted access.

2) First, if the user is not authenticated (no profile) and if some clients have been defined in the `clients` parameter, a login is tried for the direct clients.

3) Then, if the user has a profile, authorizations are checked according to the `authorizers` configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.

4) Finally, if the user is still not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the `clients` configuration. Otherwise, a 401 error page is displayed.

#### Setup with annotations

In order to bind the filter to an URL, it must be bound to a JAX-RS Resource method using the `@Pac4JSecurity` annotation.

For example:
```java
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public UserData getUserData(@Pac4JProfile CommonProfile profile) {
        LOG.debug("Returning infos for {}", profile.getId());

        return new UserData(profile.getId(), profile.getDisplayName());
    }
```

It is also possible to put `@Pac4JSecurity` directly on a class resource: the
security filter will thus apply to every method of the resource and can be
overridden with a method-level `@Pac4JSecurity` annotation (always takes
precedence) or disabled by exploiting the `ignore` property of the annotation:
```java
@Path("/class")
@Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated")
public class TestClassLevelResource {

    @GET
    @Path("no")
    @Pac4JSecurity(ignore = true)
    public String get() {
        return "ok";
    }

    @POST
    @Path("direct")
    public String direct() {
        return "ok";
    }
}
```


#### Setup with register

Another option is to register the filter into Jersey as a global filter like so:
```java
resourceConfig.register(
    new Pac4JSecurityFilterFeature(pac4jConfig, null, "isAuthenticated", null, "excludeUserSession", null));
```

`null` values are used to denote defaults, see next section.

#### Available parameters

1) `clients` (optional): the list of client names (separated by commas) used for authentication:
- in all cases, this filter requires the user to be authenticated. Thus, if the `clients` is blank or not defined, the user must have been previously authenticated
- if the `client_name` request parameter is provided, only this client (if it exists in the `clients`) is selected.

2) `authorizers` (optional): the list of authorizer names (separated by commas) used to check authorizations:
- if the `authorizers` is blank or not defined, no authorization is checked
- the following authorizers are available by default (without defining them in the configuration):
  * `isFullyAuthenticated` to check if the user is authenticated but not remembered, `isRemembered` for a remembered user, `isAnonymous` to ensure the user is not authenticated, `isAuthenticated` to ensure the user is authenticated (not necessary by default unless you use the `AnonymousClient`)
  * `hsts` to use the `StrictTransportSecurityHeader` authorizer, `nosniff` for `XContentTypeOptionsHeader`, `noframe` for `XFrameOptionsHeader `, `xssprotection` for `XSSProtectionHeader `, `nocache` for `CacheControlHeader ` or `securityHeaders` for the five previous authorizers
  * `csrfToken` to use the `CsrfTokenGeneratorAuthorizer` with the `DefaultCsrfTokenGenerator` (it generates a CSRF token and saves it as the `pac4jCsrfToken` request attribute and in the `pac4jCsrfToken` cookie), `csrfCheck` to check that this previous token has been sent as the `pac4jCsrfToken` header or parameter in a POST request and `csrf` to use both previous authorizers.

3) `matchers` (optional): the list of matcher names (separated by commas) that the request must satisfy to check authentication / authorizations

4) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default).

5) `skipResponse` (optional): by default pac4j builds an answer (in case of unauthenticated or unauthorized access), if this is set to `true` then the response will be skipped. There is no good reason to do so with the `SecurityFilter` though.

---

### 4) Define the callback endpoint only for indirect clients (`CallbackFilter`)

For indirect clients (like Facebook), the user is redirected to an external identity provider for login and then back to the application.
Thus, a callback endpoint is required in the application. It is managed by the `CallbackFilter` which has the following behaviour:

1) the credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session

2) finally, the user is redirected back to the originally requested url (or to the `defaultUrl`).

#### Setup with annotations

In order to bind the filter to an URL, it must be bound to a JAX-RS Resource method using the `@Pac4JCallback` annotation.

For example:
```java
    @GET
    @Pac4JCallback(skipResponse = true)
    public UserData loginCB(@Pac4JProfile CommonProfile profile) {
        if (profile != null) {
            return new UserData(profile.getId(), profile.getDisplayName());
        } else {
            throw new WebApplicationException(401);
        }
    }
```

#### Available parameters

1) `defaultUrl` (optional): it's the default url after login if no url was originally requested (`/` by default)

2) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default)

3) `renewSession` (optional): it indicates whether the web session must be renewed after login, to avoid session hijacking (`true` by default).

4) `skipResponse` (optional): by default pac4j builds an answer (to redirect to the originally requested url), if this is set to `true` then the response will be skipped. Coupled with the `CommonProfile` parameter injection (see below), it can be useful to implement the desired answer (for example 401) in the resource method.

---

### 5) Get the user profile (`CommonProfile` and `ProfileManager`)

When using Jersey as the JAX-RS runtime, it is possible to directly inject a pac4j profile or profile manager using method parameters injection.
When using another JAX-RS runtime, see below for workarounds.

#### Using method parameters injection

You can get the profile of the authenticated user using the annotation `@Pac4JProfile` like so:

```java
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserData getUserData(@Pac4JProfile CommonProfile profile) {
        LOG.debug("Returning infos for {}", profile.getId());

        return new UserData(profile.getId(), profile.getDisplayName());
    }
```

It has one parameter name `readFromSession` (default is `true`: use `false` not to use the session, but only the current HTTP request, useful in particular with the session-less `JaxRsContextFactoryProvider`).

You can also get the profile manager (which gives access to more advanced information about the profile) using the annotation `@Pac4JProfileManager` like so:

```java
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserData getUserData(@Pac4JProfileManager ProfileManager<CommonProfile> profileM) {

        final CommonProfile profile = profileM.get(true).get();

        LOG.debug("Returning infos for {}", profile.getId());

        return new UserData(profile.getId(), profile.getDisplayName());
    }
```

You can test if the user is authenticated using `profileManager.isAuthenticated()`.
You can get all the profiles of the authenticated user (if ever multiple ones are kept) using `profileManager.getAll(true)`.

The retrieved profile is at least a `CommonProfile`, from which you can retrieve the most common attributes that all profiles share. But you can also cast the user profile to the appropriate profile according to the provider used for authentication. For example, after a Facebook authentication:

```java
FacebookProfile facebookProfile = (FacebookProfile) commonProfile;
```

or even:
```java
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserData getUserData(@Pac4JProfile FacebookProfile profile) {
        LOG.debug("Returning infos for {}", profile.getId());

        return new UserData(profile.getId(), profile.getDisplayName());
    }
```

#### Without method parameters injection

**Help wanted**: if you want to implement method parameters injection for other frameworks than Jersey, help will be appreciated (for Resteasy [for example](https://github.com/pac4j/jax-rs-pac4j/issues/6)).

If using a JAX-RS runtime running on top of a Servlet container, it is always possible to simply exploit the `HttpServletRequest` as explained [there](https://github.com/pac4j/j2e-pac4j#5-get-the-user-profile-profilemanager):
```java
    @GET
    public void get(@Context HttpServletRequest request) {
        ProfileManager manager = new ProfileManager(new J2EContext(request, null));
        Optional<CommonProfile> profile = manager.get(true);
    }
```

---

### 6) Logout (`ApplicationLogoutFilter`)

You can log out the current authenticated user using the `ApplicationLogoutFilter`. It has the following behaviour:

1) after logout, the user is redirected to the url defined by the `url` request parameter if it matches the `logoutUrlPattern`

2) or the user is redirected to the `defaultUrl` if it is defined

3) otherwise, a blank page is displayed.

#### Setup with annotations

In order to bind the filter to an URL, it must be bound to a JAX-RS Resource method using the `@Pac4JLogout` annotation.

For example:
```java
    @DELETE
    @Path("/session")
    @Pac4JLogout(skipResponse = true)
    public void logout() {
        // do nothing
    }
```

#### Available parameters

1) `defaultUrl` (optional): the default logout url if no `url` request parameter is provided or if the `url` does not match the `logoutUrlPattern` (not defined by default)

2) `logoutUrlPattern` (optional): the logout url pattern that the `url` parameter must match (only relative urls are allowed by default).

3) `skipResponse` (optional): by default pac4j builds an answer (to redirect to the logout url), if this is set to `true` then the response will be skipped. In the case of RESTful APIs, it can make sense to not use redirection.

---

## Release notes

See the [release notes](https://github.com/pac4j/jax-rs-pac4j/wiki/Release-Notes). Learn more by browsing the [jax-rs-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/jax-rs-pac4j/1.2.1) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/1.9.6/index.html).


## Need help?

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)


## Development

The version 2.0.0-SNAPSHOT is under development.

Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/jax-rs-pac4j.png?branch=master)](https://travis-ci.org/pac4j/jax-rs-pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). This repository must be added in the Maven `pom.xml` file for example:

```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
