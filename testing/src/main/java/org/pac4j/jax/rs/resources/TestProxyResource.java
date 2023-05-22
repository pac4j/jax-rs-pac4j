package org.pac4j.jax.rs.resources;

import java.lang.reflect.InvocationTargetException;

import jakarta.ws.rs.Path;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * See https://github.com/pac4j/jax-rs-pac4j/issues/10 to understand this test
 *
 * @author Victor Noel - Linagora
 * @since 1.0.1
 *
 */
@Path("/")
public class TestProxyResource {

    @Path("proxied/class")
    public TestClassLevelResource proxiedResource() {
        try {
            final ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(TestClassLevelResource.class);
            final Proxy proxy = (Proxy) factory.createClass().getConstructor().newInstance();
            proxy.setHandler((self, overridden, proceed, args) -> {
                return proceed.invoke(self, args);
            });

            return (TestClassLevelResource) proxy;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new AssertionError(e);
        }
    }
}
