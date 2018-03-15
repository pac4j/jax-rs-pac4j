package org.pac4j.jax.rs.rules;

import org.assertj.core.util.Sets;
import org.glassfish.grizzly.http.server.util.Enumerator;
import org.junit.rules.TestRule;
import org.pac4j.jax.rs.TestConfig;
import org.pac4j.jax.rs.resources.TestClassLevelResource;
import org.pac4j.jax.rs.resources.TestProxyResource;
import org.pac4j.jax.rs.resources.TestResource;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

public interface ContainerRule extends TestRule, TestConfig {

    WebTarget getTarget(String url);

    String cookieName();

    default void setUpClientClassloader(Class<? extends ClientBuilder> clazz) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Thread.currentThread().setContextClassLoader(new ClassLoader() {
                    @Override
                    public Enumeration<URL> getResources(String name) throws IOException {
                        if (("META-INF/services/" + ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY).equalsIgnoreCase(name)) {
                            File file = File.createTempFile("ClientBuilderProvider", "");

                            writeProviderFile(file);

                            URL url = file.toURI().toURL();
                            return new Enumerator<>(Collections.singletonList(url));
                        } else {
                            return super.getResources(name);
                        }
                    }

                    public void writeProviderFile(File file) throws IOException {
                        if(!file.exists()){
                            file.delete();
                        }

                        file.createNewFile();

                        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                            writer.write(clazz.getName());
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
