package org.pac4j.jax.rs;

import org.junit.Ignore;
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

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void testInject() {
        // do nothing
    }

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void directInject() {
        // do nothing
    }

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void directInjectSkipFail() {
        // do nothing
    }

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void directInjectSkipOk() {
        // do nothing
    }

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void directInjectManagerAuth() {
        // do nothing
    }

    // TODO we don't have injection yet for something else than Jersey!
    @Ignore
    @Override
    public void directInjectManagerNoAuth() {
        // do nothing
    }
}
