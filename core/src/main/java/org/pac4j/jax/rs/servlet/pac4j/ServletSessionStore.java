package org.pac4j.jax.rs.servlet.pac4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

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

    protected ServletSessionStore() {
    }

    protected ServletSessionStore(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public HttpSession getHttpSession(WebContext context) {
        assert context instanceof ServletJaxRsContext;
        return ((ServletJaxRsContext) context).getRequest().getSession();
    }

    @Override
    public Optional<Object> get(WebContext context, String key) {
        return Optional.ofNullable(getHttpSession(context).getAttribute(key));
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        if (value == null) {
            getHttpSession(context).removeAttribute(key);
        } else {
            getHttpSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(WebContext context) {
        final HttpSession session = getHttpSession(context);

        session.invalidate();

        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        return Optional.ofNullable(getHttpSession(context));
    }

    @Override
    public boolean renewSession(WebContext context) {
        final HttpSession session = getHttpSession(context);
        final Map<String, Object> attributes = new HashMap<>();
        Collections.list(session.getAttributeNames()).forEach(k -> attributes.put(k, session.getAttribute(k)));

        session.invalidate();

        // let's recreate the session from zero, the previous becomes
        // generally unusable depending on the servlet implementation
        final HttpSession newSession = getHttpSession(context);
        attributes.forEach(newSession::setAttribute);

        return true;
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
