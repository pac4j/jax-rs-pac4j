package org.pac4j.jax.rs.rules;

import java.util.Set;

import org.pac4j.jax.rs.resources.TestSessionResource;

public interface SessionContainerRule extends ContainerRule {
    
    @Override
    default Set<Class<?>> getResources() {
        Set<Class<?>> resources = ContainerRule.super.getResources();
        resources.add(TestSessionResource.class);
        return resources;
    }
}
