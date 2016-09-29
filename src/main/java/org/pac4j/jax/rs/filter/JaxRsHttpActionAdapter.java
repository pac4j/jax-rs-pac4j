package org.pac4j.jax.rs.filter;

import org.pac4j.core.http.HttpActionAdapter;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public enum JaxRsHttpActionAdapter implements HttpActionAdapter<Object, JaxRsContext> {
    ADAPT(false), SKIP(true);

    private boolean skip;

    private JaxRsHttpActionAdapter(boolean skip) {
        this.skip = skip;
    }

    @Override
    public Object adapt(int code, JaxRsContext context) {
        if (!skip) {
            context.getRequestContext().abortWith(context.getAbortBuilder().build());
        }
        return null;
    }

}
