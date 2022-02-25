package org.pac4j.jax.rs.rules;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.mockito.Mockito;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.resources.JerseyResource;

public class JerseyInMemoryRule extends JerseyRule {

    @Override
    public Set<Class<?>> getResources() {
        Set<Class<?>> resources = super.getResources();
        resources.add(JerseyResource.class);
        return resources;
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment(ResourceConfig config) {
        return DeploymentContext.builder(config).build();
    }

    protected ResourceConfig configureResourceConfig(ResourceConfig config) {
        final Config pac4jConfig = getConfig();
        // we create a fake session to make tests pass. Otherwise we would need: matchers="none"
        // or pac4j should be able to handle no session store.
        pac4jConfig.setSessionStore(Mockito.mock(SessionStore.class));
        return config
            .register(new JaxRsConfigProvider(pac4jConfig))
            .register(new Pac4JSecurityFeature())
            .register(new Pac4JValueFactoryProvider.Binder())
            .register(JaxRsContextFactoryProvider.class);
    }
}
