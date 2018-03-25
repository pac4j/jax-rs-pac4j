package org.pac4j.jax.rs.rules;

import java.net.CookieHandler;
import java.net.CookieManager;

import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.util.Globals;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.rules.ExternalResource;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;

public abstract class JerseyRule extends ExternalResource implements ContainerRule {

    private MyJerseyTest jersey;

    public class MyJerseyTest extends JerseyTest {

        @Override
        protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
            return JerseyRule.this.getTestContainerFactory();
        }

        @Override
        protected DeploymentContext configureDeployment() {
            forceSet(TestProperties.CONTAINER_PORT, "0");

            return JerseyRule.this.configureDeployment(configureResourceConfig(new ResourceConfig(getResources())));
        }
    }

    protected ResourceConfig configureResourceConfig(ResourceConfig config) {
        return config
                .register(new JaxRsConfigProvider(getConfig()))
                .register(new Pac4JSecurityFeature())
                .register(new Pac4JValueFactoryProvider.Binder());
    }

    protected abstract TestContainerFactory getTestContainerFactory();

    protected abstract DeploymentContext configureDeployment(ResourceConfig config);

    @Override
    protected void before() throws Throwable {
        // Used by Jersey Client to store cookies
        CookieHandler.setDefault(new CookieManager());

        jersey = new MyJerseyTest();
        jersey.setUp();
    }

    @Override
    protected void after() {
        try {
            jersey.tearDown();
            CookieHandler.setDefault(null);
        } catch (Exception e) {
            throw new RuntimeException("can't stop jersey", e);
        }
    }

    @Override
    public WebTarget getTarget(String url) {
        return jersey.target(url);
    }

    @Override
    public String cookieName() {
        return Globals.SESSION_COOKIE_NAME;
    }
}
