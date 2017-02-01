package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultCallbackLogic;

/**
 * 
 * Simply delegates session renewal to the {@link JaxRsSessionStore} that extends pac4j {@link SessionStore}. This could
 * bee included directly in pac4j, see https://github.com/pac4j/pac4j/issues/711
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 * 
 */
public class JaxRsRenewSessionCallbackLogic<C extends JaxRsContext> extends DefaultCallbackLogic<Object, C> {

    public JaxRsRenewSessionCallbackLogic() {
        setProfileManagerFactory(JaxRsProfileManager::new);
    }

    @Override
    protected void renewSession(final JaxRsContext context) {
        final JaxRsSessionStore sessionStore = context.getSessionStore();
        if (sessionStore != null) {
            logger.debug("Discard old session and replace by a new one...");
            sessionStore.renewSession(context);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
