package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jax.rs.rules.SessionContainerRule;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractSessionTest extends AbstractTest {

    @Override
    protected abstract SessionContainerRule createContainer();

    @Test
    public void testNotLogged() {
        final Response res = container.getTarget("/session/logged").request().get();
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    public void testLogin() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String login = container.getTarget("/session/login")
                .queryParam(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "FormClient").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(login).isEqualTo("ok");
    }

    @Test
    public void testInject() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final String login = container.getTarget("/session/login")
                .queryParam(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "FormClient").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        assertThat(login).isEqualTo("ok");

        final String ok = container.getTarget("/session/inject").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testLoginFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response res = container.getTarget("/session/login")
                .queryParam(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "FormClient").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(res.getStatus()).isEqualTo(401);

    }
}
