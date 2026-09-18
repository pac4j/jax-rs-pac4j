package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.Test;
import org.pac4j.jax.rs.rules.RestEasyUndertowServletRule;
import org.pac4j.jax.rs.rules.SessionContainerRule;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class RestEasyUndertowServletTest extends AbstractSessionTest {

    @Test
    public void servlet61RedirectPreservesBody() {
        try (Response response = container.getTarget("/containerSpecific/servlet61/redirect")
                .property(ClientProperties.FOLLOW_REDIRECTS, false).request().get()) {
            assertThat(response.getStatus()).isEqualTo(307);
            assertThat(response.getLocation().getPath()).isEqualTo("/no");
            assertThat(response.readEntity(String.class)).isEqualTo("preserved");
        }
    }

    @Override
    protected SessionContainerRule createContainer() {
        return new RestEasyUndertowServletRule();
    }

}
