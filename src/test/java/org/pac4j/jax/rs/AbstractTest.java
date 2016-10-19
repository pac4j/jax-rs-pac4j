package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.jax.rs.pac4j.JaxRsCallbackUrlResolver;
import org.slf4j.bridge.SLF4JBridgeHandler;

public abstract class AbstractTest {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Logger.getLogger("org.glassfish").setLevel(Level.FINEST);
    }

    protected void setUpClientClassloader(Class<? extends ClientBuilder> clazz) {
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

    protected Config getConfig() {
        // login not used because the ajax resolver always answer true
        Authenticator<UsernamePasswordCredentials> auth = new SimpleTestUsernamePasswordAuthenticator();
        FormClient client = new FormClient("notUsedLoginUrl", auth);
        DirectFormClient client2 = new DirectFormClient(auth);

        Clients clients = new Clients("notUsedCallbackUrl", client, client2);
        // in case of invalid credentials, we simply want the error, not a redirect to the login url
        clients.setAjaxRequestResolver((c) -> true);
        // so that callback url have the correct prefix w.r.t. the container's context
        clients.setCallbackUrlResolver(new JaxRsCallbackUrlResolver());

        Config config = new Config(clients);

        // needed by callback since we don't specify client in the URL parameter
        clients.setDefaultClient(client);
        return config;
    }

    protected Class<?> getResource() {
        return TestResource.class;
    }

    protected abstract WebTarget getTarget(String url);

    @Test
    public void testNoPac4j() {
        final String ok = getTarget("/no").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = getTarget("/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response direct = getTarget("/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(direct.getStatus()).isEqualTo(401);
    }

    @Test
    public void directInject() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = getTarget("/directInject").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInjectSkipOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = getTarget("/directInjectSkip").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInjectSkipFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final String ok = getTarget("/directInjectSkip").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("fail");
    }

}
