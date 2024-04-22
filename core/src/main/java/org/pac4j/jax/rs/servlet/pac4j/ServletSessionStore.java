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

    protected ServletSessionStore() {}

    public Optional<HttpSession> getNativeSession(final WebContext context, final boolean createSession) {
        assert context instanceof ServletJaxRsContext;

        final HttpSession nativeSession = ((ServletJaxRsContext) context).getRequest().getSession(createSession);
        return Optional.ofNullable(nativeSession);
    }

    @Override
    public Optional<Object> get(final WebContext context, final String key) {
        return getNativeSession(context, false)
            .map(it -> it.getAttribute(key));
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
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
    public boolean destroySession(final WebContext context) {
        return getNativeSession(context, false)
            .map(it -> {
                it.invalidate();
                return true;
            })
            .orElse(false);
    }

    @Override
    public Optional<Object> getTrackableSession(final WebContext context) {
        return Optional.ofNullable(getNativeSession(context, false));
    }

    @Override
    public boolean renewSession(final WebContext context) {
        return getNativeSession(context, false)
            .map(it -> {
                final Map<String, Object> attributes = new HashMap<>();
                Collections.list(it.getAttributeNames()).forEach(k -> attributes.put(k, it.getAttribute(k)));

                it.invalidate();

                // let's recreate the session from zero, the previous becomes
                // generally unusable depending on the servlet implementation
                getNativeSession(context, true)
                    .ifPresent(newSession -> attributes.forEach(newSession::setAttribute));

                return true;
            })
            .orElse(false);
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        return Optional.of(new ServletSessionStore() {
            @Override
            public Optional<HttpSession> getNativeSession(WebContext context, boolean createSession) {
                return Optional.of((HttpSession) trackableSession);
            }
        });
    }

    @Override
    public Optional<String> getSessionId(final WebContext context, final boolean createSession) {
        return getNativeSession(context, createSession).map(HttpSession::getId);
    }
}
