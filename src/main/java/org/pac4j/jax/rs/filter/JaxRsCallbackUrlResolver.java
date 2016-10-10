package org.pac4j.jax.rs.filter;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.CallbackUrlResolver;

/**
 * 
 * This can be used by applications to ensure the callback url is properly prefixed by the context where the JAX-RS
 * implementation is deployed.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsCallbackUrlResolver implements CallbackUrlResolver {

    @Override
    public String compute(String callbackUrl, WebContext context) {
        if (context instanceof JaxRsContext && callbackUrl != null) {
            return ((JaxRsContext) context).getAbsolutePath(callbackUrl, true);
        }
        return null;
    }

}
