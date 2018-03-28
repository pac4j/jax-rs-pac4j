package org.pac4j.jax.rs;

import org.pac4j.jax.rs.rules.JerseyGrizzlyServletRule;
import org.pac4j.jax.rs.rules.SessionContainerRule;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JerseyGrizzlyServletTest extends AbstractSessionTest {

    @Override
    protected SessionContainerRule createContainer() {
        return new JerseyGrizzlyServletRule();
    }
}
