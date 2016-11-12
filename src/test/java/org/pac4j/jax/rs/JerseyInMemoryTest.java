package org.pac4j.jax.rs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JerseyInMemoryTest extends AbstractTest {

    private MyJerseyTest jersey;

    @Before
    public void setUp() throws Exception {
        jersey = new MyJerseyTest();
        // let's force use a JerseyClient!
        setUpClientClassloader(JerseyClientBuilder.class);
        jersey.setUp();
    }

    public class MyJerseyTest extends JerseyTest {

        @Override
        protected TestContainerFactory getTestContainerFactory()
                throws TestContainerException {
            return new InMemoryTestContainerFactory();
        }

        @Override
        protected DeploymentContext configureDeployment() {
            Config config = getConfig();

            ResourceConfig app = new ResourceConfig(getResource())
                    .register(new JaxRsContextFactoryProvider(config))
                    .register(new Pac4JSecurityFeature(config))
                    .register(new Pac4JValueFactoryProvider.Binder());

            return DeploymentContext.builder(app).build();
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
}
