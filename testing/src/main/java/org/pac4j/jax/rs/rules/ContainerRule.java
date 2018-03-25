package org.pac4j.jax.rs.rules;

import java.util.Set;

import javax.ws.rs.client.WebTarget;

import org.assertj.core.util.Sets;
import org.junit.rules.TestRule;
import org.pac4j.jax.rs.TestConfig;
import org.pac4j.jax.rs.resources.TestClassLevelResource;
import org.pac4j.jax.rs.resources.TestProxyResource;
import org.pac4j.jax.rs.resources.TestResource;

public interface ContainerRule extends TestRule, TestConfig {

    WebTarget getTarget(String url);

    String cookieName();

    default Set<Class<?>> getResources() {
        return Sets.newLinkedHashSet(TestResource.class, TestClassLevelResource.class, TestProxyResource.class);
    }
}
