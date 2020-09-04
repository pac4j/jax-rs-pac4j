package org.pac4j.jax.rs.servlet.pac4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class ServletSessionStore implements SessionStore<JaxRsContext> {

    public HttpSession getHttpSession(JaxRsContext context) {
        assert context instanceof ServletJaxRsContext;
        return ((ServletJaxRsContext) context).getRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(JaxRsContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Optional<Object> get(JaxRsContext context, String key) {
        return Optional.of(getHttpSession(context).getAttribute(key));
    }

    @Override
    public void set(JaxRsContext context, String key, Object value) {
        if (value == null) {
            getHttpSession(context).removeAttribute(key);
        } else {
            getHttpSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(JaxRsContext context) {
        final HttpSession session = getHttpSession(context);

        session.invalidate();

        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(JaxRsContext context) {
        return Optional.of(getHttpSession(context));
    }

    @Override
    public boolean renewSession(JaxRsContext context) {
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
    public Optional<SessionStore<JaxRsContext>> buildFromTrackableSession(JaxRsContext context, Object trackableSession) {
        return Optional.of(new ServletSessionStore() {
            @Override
            public HttpSession getHttpSession(JaxRsContext context) {
                return (HttpSession) trackableSession;
            }
        });
    }
}
