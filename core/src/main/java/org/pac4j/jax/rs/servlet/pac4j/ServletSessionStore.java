package org.pac4j.jax.rs.servlet.pac4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class ServletSessionStore implements SessionStore {

    public static final ServletSessionStore INSTANCE = new ServletSessionStore();

    protected HttpSession httpSession;

    protected ServletSessionStore() {}

    protected ServletSessionStore(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public HttpSession getHttpSession(final WebContext context) {
        assert context instanceof ServletJaxRsContext;
        try {
            return ((ServletJaxRsContext) context).getRequest().getSession();
        } catch (final IllegalStateException e) {
            return null;
        }
    }

    @Override
    public Optional<Object> get(final WebContext context, final String key) {
        final HttpSession session = getHttpSession(context);

        if (session == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(session.getAttribute(key));
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        final HttpSession session = getHttpSession(context);

        if (session != null) {
            if (value == null) {
                session.removeAttribute(key);
            } else {
                session.setAttribute(key, value);
            }
        }
    }

    @Override
    public boolean destroySession(WebContext context) {
        final HttpSession session = getHttpSession(context);

        if (session != null) {
            session.invalidate();

            return true;
        }

        return false;
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        return Optional.ofNullable(getHttpSession(context));
    }

    @Override
    public boolean renewSession(WebContext context) {
        final HttpSession session = getHttpSession(context);

        if (session != null) {
            final Map<String, Object> attributes = new HashMap<>();
            Collections.list(session.getAttributeNames()).forEach(k -> attributes.put(k, session.getAttribute(k)));

            session.invalidate();

            // let's recreate the session from zero, the previous becomes
            // generally unusable depending on the servlet implementation
            final HttpSession newSession = getHttpSession(context);
            attributes.forEach(newSession::setAttribute);

            return true;
        }

        return false;
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession) {
        return Optional.ofNullable(new ServletSessionStore() {
            @Override
            public HttpSession getHttpSession(WebContext context) {
                return (HttpSession) trackableSession;
            }
        });
    }

    @Override
    public Optional<String> getSessionId(WebContext context, boolean createSession) {
        HttpSession session = getHttpSession(context);
        return (session != null) ? Optional.of(session.getId()) : Optional.empty();
    }
}
