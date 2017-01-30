package org.pac4j.jax.rs.filters;

import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.1.1
 *
 */
public class JaxRsHttpActionAdapter implements HttpActionAdapter<Object, JaxRsContext> {

    public static final JaxRsHttpActionAdapter INSTANCE = new JaxRsHttpActionAdapter();

    @Override
    public Object adapt(int code, JaxRsContext context) {
        context.getRequestContext().abortWith(context.getAbortBuilder().build());
        return null;
    }

}
