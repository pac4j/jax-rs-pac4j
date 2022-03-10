package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;

/**
 * 
 * This can be used by applications to ensure that pac4j always answers with 401
 * instead of redirect.
 * 
 * @author Victor Noel
 * @since 3.0.0
 *
 */
public class JaxRsAjaxRequestResolver extends DefaultAjaxRequestResolver {
    @Override
    public boolean isAjax(WebContext context, SessionStore sessionStore) {
        return true;
    }
}
