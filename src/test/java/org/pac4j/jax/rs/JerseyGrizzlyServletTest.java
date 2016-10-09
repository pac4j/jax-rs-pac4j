package org.pac4j.jax.rs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.util.Globals;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.features.jersey.Pac4JValueFactoryProvider;

public class JerseyGrizzlyServletTest extends AbstractTest {

    private MyJerseyTest jersey;

    @Before
    public void setUp() throws Exception {
        jersey = new MyJerseyTest();
        jersey.setUp();
        // let's force use a JerseyClient!
        jersey.setClient(new JerseyClientBuilder().build());
    }

    public class MyJerseyTest extends JerseyTest {

        @Override
        protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
            return new GrizzlyWebTestContainerFactory();
        }

        @Override
        protected DeploymentContext configureDeployment() {
            forceSet(TestProperties.CONTAINER_PORT, "0");

            Config config = getConfig();
            ResourceConfig app = new ResourceConfig(TestResource.class).register(new Pac4JSecurityFeature(config))
                    .register(new Pac4JValueFactoryProvider.Binder(config));

            return ServletDeploymentContext.forServlet(new ServletContainer(app)).build();
        }

        @SuppressWarnings("UselessOverridingMethod")
        @Override
        protected Client setClient(Client client) {
            return super.setClient(client);
        }
    }

    @After
    public void tearDown() throws Exception {
        jersey.tearDown();
    }

    @Override
    protected WebTarget getTarget(String url) {
        return jersey.target(url).property(ClientProperties.FOLLOW_REDIRECTS, false);
    }

    @Override
    protected String cookieName() {
        return Globals.SESSION_COOKIE_NAME;
    }
}
