package org.pac4j.jax.rs.filters;

import javax.ws.rs.core.Response;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
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
    public Object adapt(final HttpAction action, final JaxRsContext context) {
        if (action != null) {
            final int code = action.getCode();
            context.getAbortBuilder().status(code);
            context.getResponseHolder().setResponseStatus(code);
            if (action instanceof WithLocationAction) {
                final WithLocationAction withLocationAction = (WithLocationAction) action;
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());
            } else if (action instanceof WithContentAction) {
                final String content = ((WithContentAction) action).getContent();
                context.getAbortBuilder().entity(content);
                context.getResponseHolder().writeResponseContent(content);
            }
            Response response = context.getAbortBuilder().build();
            context.getRequestContext().abortWith(response);
            return null;
        }

        throw new TechnicalException("No action provided");
    }
}
