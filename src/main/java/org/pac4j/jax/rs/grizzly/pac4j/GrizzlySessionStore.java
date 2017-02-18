package org.pac4j.jax.rs.grizzly.pac4j;

import java.util.HashMap;
import java.util.Map;

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
    public Object get(JaxRsContext context, String key) {
        return getSession(context).getAttribute(key);
    }

    @Override
    public void set(JaxRsContext context, String key, Object value) {
        getSession(context).setAttribute(key, value);
    }
    
    @Override
    public boolean destroySession(JaxRsContext context) {
        final Session session = getSession(context);
        
        session.setValid(false);
        
        return true;
    }
    
    @Override
    public Object getTrackableSession(JaxRsContext context) {
        return getSession(context);
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
    public SessionStore<JaxRsContext> buildFromTrackableSession(JaxRsContext context, Object trackableSession) {
        return new GrizzlySessionStore() {
            @Override
            public Session getSession(JaxRsContext context) {
                return (Session) trackableSession;
            }
        };
    }
}
