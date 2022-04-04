package org.pac4j.jax.rs.rules;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.time.Duration;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.assertj.core.util.Sets;
import org.awaitility.Awaitility;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.weld.environment.servlet.Listener;
import org.junit.rules.ExternalResource;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.resources.RestEasyResource;
import org.pac4j.jax.rs.resteasy.features.Pac4JProfileInjectorFactory;
import org.pac4j.jax.rs.servlet.features.Pac4JServletFeature;

import io.undertow.server.session.SessionCookieConfig;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;

public class RestEasyUndertowServletRule extends ExternalResource implements SessionContainerRule {

    private UndertowJaxrsServer server;

    private Client client;

    public class MyApp extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> classes = getResources();
            classes.add(Pac4JProfileInjectorFactory.class);
            return classes;
        }

        @Override
        public Set<Object> getSingletons() {
            return Sets.newLinkedHashSet(
                    new Pac4JServletFeature(getConfig()),
                    new Pac4JSecurityFeature());
        }
    }

    @Override
    public Set<Class<?>> getResources() {
        Set<Class<?>> resources = SessionContainerRule.super.getResources();
        resources.add(RestEasyResource.class);
        return resources;
    }

    @Override
    protected void before() throws Throwable {
        // Used by Jersey Client to store cookies
        CookieHandler.setDefault(new CookieManager());

        // we don't need a resteasy client, and the jersey one works better with redirect
        client = new JerseyClientBuilder().build();

        // TODO use an autogenerated port...
        System.setProperty("org.jboss.resteasy.port", "24257");
        server = new UndertowJaxrsServer().start();

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        deployment.setApplication(new MyApp());
        DeploymentInfo di = server.undertowDeployment(deployment)
                .setContextPath("/")
                .setDeploymentName("DI")
                .setClassLoader(getClass().getClassLoader())
                .addListeners(Servlets.listener(Listener.class));
        server.deploy(di);
    }

    @Override
    protected void after() {
        server.stop();
        // server.stop is not instantaneous
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> {
            try {
                getTarget("/").request().get();
            } catch (ProcessingException e) {
                return true;
            }
            return false;
        });
        client.close();
        CookieHandler.setDefault(null);
    }

    @Override
    public WebTarget getTarget(String url) {
        return client.target(TestPortProvider.generateURL(url));
    }

    @Override
    public String cookieName() {
        return SessionCookieConfig.DEFAULT_SESSION_ID;
    }
}