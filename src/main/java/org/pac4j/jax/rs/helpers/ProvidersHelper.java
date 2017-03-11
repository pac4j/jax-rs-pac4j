package org.pac4j.jax.rs.helpers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.util.CommonHelper;

/**
 * @author Victor Noel - Linagora
 * @since 2.0.0
 */
public class ProvidersHelper {

    public ProvidersHelper() {
    }
    
    public static <A> A getContext(Providers providers, Class<A> clazz) {
        ContextResolver<A> cr = providers.getContextResolver(clazz, MediaType.WILDCARD_TYPE);
        CommonHelper.assertNotNull("ContextResolver<"+clazz.getSimpleName()+">", cr);
        A a = cr.getContext(null);
        CommonHelper.assertNotNull(clazz.getSimpleName(), a);
        return a;
    }
}
