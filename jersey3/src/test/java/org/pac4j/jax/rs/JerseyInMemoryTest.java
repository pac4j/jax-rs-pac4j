package org.pac4j.jax.rs;

import org.pac4j.jax.rs.rules.ContainerRule;
import org.pac4j.jax.rs.rules.JerseyInMemoryRule;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JerseyInMemoryTest extends AbstractTest {
    @Override
    protected ContainerRule createContainer() {
        return new JerseyInMemoryRule();
    }
}
