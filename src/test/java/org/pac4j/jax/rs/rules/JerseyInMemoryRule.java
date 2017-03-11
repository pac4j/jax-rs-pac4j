package org.pac4j.jax.rs.rules;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;

public class JerseyInMemoryRule extends JerseyRule {

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment(ResourceConfig config) {
        return DeploymentContext.builder(config).build();
    }

    @Override
    protected ResourceConfig configureResourceConfig(ResourceConfig config) {
        return super
                .configureResourceConfig(config)
                .register(new JaxRsContextFactoryProvider());
    }

}
