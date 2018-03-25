package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.UrlResolver;

/**
 * 
 * This can be used by applications to ensure the callback URL is properly prefixed by the context where the JAX-RS
 * implementation is deployed.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsUrlResolver implements UrlResolver {

    @Override
    public String compute(String url, WebContext context) {
        if (context instanceof JaxRsContext && url != null) {
            return ((JaxRsContext) context).getAbsolutePath(url, true);
        }
        return null;
    }

}
