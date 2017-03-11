package org.pac4j.jax.rs.rules;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Sets;
import org.junit.rules.TestRule;
import org.pac4j.jax.rs.TestConfig;
import org.pac4j.jax.rs.resources.TestClassLevelResource;
import org.pac4j.jax.rs.resources.TestProxyResource;
import org.pac4j.jax.rs.resources.TestResource;

public interface ContainerRule extends TestRule, TestConfig {
    
    WebTarget getTarget(String url);

    String cookieName();
    
    default void setUpClientClassloader(Class<? extends ClientBuilder> clazz) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Thread.currentThread().setContextClassLoader(new ClassLoader() {
                    @Override
                    public InputStream getResourceAsStream(String name) {
                        if (("META-INF/services/" + ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY).equals(name)) {
                            return IOUtils.toInputStream(clazz.getName());
                        } else {
                            return super.getResourceAsStream(name);
                        }
                    }
                });
                return null;
            }
        });
    }
    
    default Set<Class<?>> getResources() {
        return Sets.newLinkedHashSet(
                TestResource.class,
                TestClassLevelResource.class,
                TestProxyResource.class);
    }
}
