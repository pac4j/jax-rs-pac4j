package org.pac4j.jax.rs.rules;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.pac4j.jax.rs.grizzly.features.GrizzlyJaxRsContextFactoryProvider;

public class JerseyGrizzlyRule extends JerseyRule implements SessionContainerRule {

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment(ResourceConfig config) {
        return DeploymentContext.builder(config).build();
    }

    @Override
    protected ResourceConfig configureResourceConfig(ResourceConfig config) {
        return super
                .configureResourceConfig(config)
                .register(new GrizzlyJaxRsContextFactoryProvider());
    }

}
