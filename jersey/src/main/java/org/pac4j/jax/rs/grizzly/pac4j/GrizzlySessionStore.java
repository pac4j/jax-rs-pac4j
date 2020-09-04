package org.pac4j.jax.rs.grizzly.pac4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.glassfish.grizzly.http.server.Session;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class GrizzlySessionStore implements SessionStore<JaxRsContext> {

    public Session getSession(final JaxRsContext context) {
        assert context instanceof GrizzlyJaxRsContext;
        return ((GrizzlyJaxRsContext) context).getRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(JaxRsContext context) {
        return getSession(context).getIdInternal();
    }

    @Override
    public Optional<Object> get(JaxRsContext context, String key) {
        return Optional.ofNullable(getSession(context).getAttribute(key));
    }

    @Override
    public void set(JaxRsContext context, String key, Object value) {
        if (value == null) {
            getSession(context).removeAttribute(key);
        } else {
            getSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(JaxRsContext context) {
        final Session session = getSession(context);

        session.setValid(false);

        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(JaxRsContext context) {
        return Optional.ofNullable(getSession(context));
    }

    @Override
    public boolean renewSession(JaxRsContext context) {
        final Session session = getSession(context);
        final Map<String, Object> attributes = new HashMap<>();
        attributes.putAll(session.attributes());

        session.setValid(false);

        // let's recreate the session from zero
        // (Grizzly reuse the same object, but that could change in the future...)
        final Session newSession = getSession(context);
        attributes.forEach(newSession::setAttribute);

        return true;
    }



    @Override
    public Optional<SessionStore<JaxRsContext>> buildFromTrackableSession(JaxRsContext context, Object trackableSession) {
        return Optional.of(new GrizzlySessionStore() {
            @Override
            public Session getSession(JaxRsContext context) {
                return (Session) trackableSession;
            }
        });
    }
}
