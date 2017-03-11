package org.pac4j.jax.rs.rules;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;

public class JerseyGrizzlyServletRule extends JerseyRule implements SessionContainerRule {

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
                .register(new ServletJaxRsContextFactoryProvider());
    }

}
