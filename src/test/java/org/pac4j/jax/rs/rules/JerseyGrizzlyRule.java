package org.pac4j.jax.rs.rules;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.pac4j.jax.rs.grizzly.features.GrizzlyJaxRsContextFactoryProvider;
import org.pac4j.jax.rs.resources.JerseyResource;

public class JerseyGrizzlyRule extends JerseyRule implements SessionContainerRule {

    @Override
    public Set<Class<?>> getResources() {
        Set<Class<?>> resources = SessionContainerRule.super.getResources();
        resources.add(JerseyResource.class);
        return resources;
    }

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
