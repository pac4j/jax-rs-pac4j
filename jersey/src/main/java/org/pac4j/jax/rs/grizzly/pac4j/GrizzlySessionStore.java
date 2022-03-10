package org.pac4j.jax.rs.grizzly.pac4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.glassfish.grizzly.http.server.Session;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class GrizzlySessionStore implements SessionStore {

    public static final GrizzlySessionStore INSTANCE = new GrizzlySessionStore();

    protected Session session;

    protected GrizzlySessionStore() {
    }

    protected GrizzlySessionStore(final Session httpSession) {
        this.session = httpSession;
    }

    public Session getSession(final WebContext context) {
        assert context instanceof GrizzlyJaxRsContext;
        return ((GrizzlyJaxRsContext) context).getRequest().getSession();
    }

    @Override
    public Optional<String> getSessionId(WebContext context, boolean createSession) {
        Session session = getSession(context);
        return (session != null) ? Optional.of(session.getIdInternal()) : Optional.empty();
    }

    @Override
    public Optional<Object> get(WebContext context, String key) {
        return Optional.ofNullable(getSession(context).getAttribute(key));
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        if (value == null) {
            getSession(context).removeAttribute(key);
        } else {
            getSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(WebContext context) {
        final Session session = getSession(context);

        session.setValid(false);

        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        return Optional.ofNullable(getSession(context));
    }

    @Override
    public boolean renewSession(WebContext context) {
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
    public Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession) {
        return Optional.of(new GrizzlySessionStore() {
            @Override
            public Session getSession(WebContext context) {
                return (Session) trackableSession;
            }
        });
    }
}
