package org.pac4j.jax.rs.rules;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.pac4j.jax.rs.resources.JerseyResource;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;

public class JerseyGrizzlyServletRule extends JerseyRule implements SessionContainerRule {

    @Override
    public Set<Class<?>> getResources() {
        Set<Class<?>> resources = SessionContainerRule.super.getResources();
        resources.add(JerseyResource.class);
        return resources;
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment(ResourceConfig config) {
        return ServletDeploymentContext.forServlet(new ServletContainer(config)).build();
    }

    @Override
    protected ResourceConfig configureResourceConfig(ResourceConfig config) {
        return super
                .configureResourceConfig(config)
                .register(ServletJaxRsContextFactoryProvider.class);
    }

}
