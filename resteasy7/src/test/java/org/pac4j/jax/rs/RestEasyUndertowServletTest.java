package org.pac4j.jax.rs;

import org.pac4j.jax.rs.rules.RestEasyUndertowServletRule;
import org.pac4j.jax.rs.rules.SessionContainerRule;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class RestEasyUndertowServletTest extends AbstractSessionTest {

    @Override
    protected SessionContainerRule createContainer() {
        return new RestEasyUndertowServletRule();
    }

}
