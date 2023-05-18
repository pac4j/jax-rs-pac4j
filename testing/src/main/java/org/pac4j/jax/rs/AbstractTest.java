package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.Rule;
import org.junit.Test;
import org.pac4j.jax.rs.rules.ContainerRule;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractTest {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Logger.getLogger("org.glassfish").setLevel(Level.FINEST);
    }

    @Rule
    public ContainerRule container = createContainer();

    protected abstract ContainerRule createContainer();

    @Test
    public void noPac4j() {
        final String ok = container.getTarget("/no").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void classLevelNoPac4j() {
        final String ok = container.getTarget("/class/no").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void proxiedClassLevelNoPac4j() {
        final String ok = container.getTarget("/proxied/class/no").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directOkComplexContentType() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/direct").request().post(
                Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE.withCharset("UTF-8")), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void defaultDirectOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/defaultDirect").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void classLevelDirectOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/class/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void proxiedClassLevelDirectOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/proxied/class/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response direct = container.getTarget("/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(direct.getStatus()).isEqualTo(401);
    }

    @Test
    public void classLevelDirectFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response direct = container.getTarget("/class/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(direct.getStatus()).isEqualTo(401);
    }

    @Test
    public void proxiedClassLevelDirectFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response direct = container.getTarget("/proxied/class/direct").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(direct.getStatus()).isEqualTo(401);
    }

    @Test
    public void directContext() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/directContext").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInject() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/directInject").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInjectNoAuth() {
        final Response res = container.getTarget("/directInjectNoAuth").request().get();
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    public void directInjectManagerAuth() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/directInjectManager").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInjectManagerNoAuth() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final String ok = container.getTarget("/directInjectManager").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("fail");
    }

    @Test
    public void directInjectSkipOk() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/directInjectSkip").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void directInjectSkipFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final String ok = container.getTarget("/directInjectSkip").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("fail");
    }

    @Test
    public void directResponseHeadersSet() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final Response ok = container.getTarget("/directResponseHeadersSet").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(ok.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat(ok.readEntity(String.class)).isEqualTo("ok");
        assertThat(ok.getHeaderString("X-Content-Type-Options")).isEqualTo("nosniff");
    }

    @Test
    public void containerSpecificSecurityContext() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/containerSpecific/securitycontext").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void containerSpecificContext() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/containerSpecific/context").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void containerSpecificSessionStore() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String ok = container.getTarget("/containerSpecific/sessionstore").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(ok).isEqualTo("ok");
    }
}
