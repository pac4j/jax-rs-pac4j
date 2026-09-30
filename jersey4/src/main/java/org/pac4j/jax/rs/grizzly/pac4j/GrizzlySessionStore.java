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
        return getSession(context, true);
    }

    public Session getSession(final WebContext context, final boolean createSession) {
        assert context instanceof GrizzlyJaxRsContext;
        return ((GrizzlyJaxRsContext) context).getRequest().getSession(createSession);
    }

    protected Optional<Session> getNativeSession(final WebContext context, final boolean createSession) {
        return Optional.ofNullable(getSession(context, createSession));
    }

    @Override
    public Optional<String> getSessionId(WebContext context, boolean createSession) {
        return getNativeSession(context, createSession).map(Session::getIdInternal);
    }

    @Override
    public Optional<Object> get(WebContext context, String key) {
        return getNativeSession(context, false).map(it -> it.getAttribute(key));
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        getNativeSession(context, value != null)
            .ifPresent(it -> {
                if (value == null) {
                    it.removeAttribute(key);
                } else {
                    it.setAttribute(key, value);
                }
            });
    }

    @Override
    public boolean destroySession(WebContext context) {
        return getNativeSession(context, false)
            .map(it -> {
                it.setValid(false);
                return true;
            })
            .orElse(false);
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        return getNativeSession(context, false).map(Object.class::cast);
    }

    @Override
    public boolean renewSession(WebContext context) {
        return getNativeSession(context, false)
            .map(it -> {
                final Map<String, Object> attributes = new HashMap<>(it.attributes());

                it.setValid(false);

                // let's recreate the session from zero
                // (Grizzly reuse the same object, but that could change in the future...)
                getNativeSession(context, true)
                    .ifPresent(newSession -> attributes.forEach(newSession::setAttribute));

                return true;
            })
            .orElse(false);
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession) {
        return Optional.of(new GrizzlySessionStore() {
            @Override
            public Session getSession(WebContext context, boolean createSession) {
                return (Session) trackableSession;
            }
        });
    }
}
