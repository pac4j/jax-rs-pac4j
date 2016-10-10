package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.filter.JaxRsCallbackUrlResolver;

public abstract class AbstractTest {

    @Path("/")
    public static class TestResource {

        @GET
        @Path("no")
        public String get() {
            return "ok";
        }

        @GET
        @Path("logged")
        @Pac4JSecurity(authorizers = "isAuthenticated")
        public String logged() {
            return "ok";
        }

        @GET
        @Path("inject")
        @Pac4JSecurity(authorizers = "isAuthenticated")
        public String inject(@Pac4JProfile CommonProfile profile) {
            if (profile != null) {
                return "ok";
            } else {
                return "error";
            }
        }

        @POST
        @Path("login")
        // TODO apparently we need to disable session renewal because grizzly
        // send 2 JSESSIONID if not...
        @Pac4JCallback(defaultUrl = "/logged", renewSession = false)
        public void login() {

        }
    }

    protected Config getConfig() {
        // login not used because the ajax resolver always answer true
        FormClient client = new FormClient("notUsedLoginUrl", new SimpleTestUsernamePasswordAuthenticator());

        Clients clients = new Clients("notUsedCallbackUrl", client);
        // in case of invalid credentials, we simply want the error, not a redirect to the login url
        clients.setAjaxRequestResolver((c) -> true);
        // not really used for now
        clients.setCallbackUrlResolver(new JaxRsCallbackUrlResolver());

        Config config = new Config(clients);

        // needed by callback since we don't specify client in the URL parameter
        clients.setDefaultClient(client);
        return config;
    }

    protected abstract WebTarget getTarget(String url);

    protected abstract String cookieName();

    @Test
    public void testNoPac4j() {
        final String ok = getTarget("/no").request().get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testNotLogged() {
        final Response res = getTarget("/logged").request().get();
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    public void testLogin() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final Response login = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(login.getStatus()).isEqualTo(302);

        final NewCookie cookie = login.getCookies().get(cookieName());
        assertThat(cookie).isNotNull();

        final String ok = getTarget("/logged").request().cookie(cookie).get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testInject() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final Response login = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(login.getStatus()).isEqualTo(302);

        final NewCookie cookie = login.getCookies().get(cookieName());
        assertThat(cookie).isNotNull();

        final String ok = getTarget("/inject").request().cookie(cookie).get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testLoginFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response res = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(res.getStatus()).isEqualTo(403);

    }
}
