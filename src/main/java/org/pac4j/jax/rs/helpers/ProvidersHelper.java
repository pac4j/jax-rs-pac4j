package org.pac4j.jax.rs.helpers;

import javax.ws.rs.ext.Providers;

/**
 * @author Victor Noel - Linagora
 * @since 2.0.0
 * @deprecated Use {@link ProvidersContext} instead
 */
@Deprecated
public class ProvidersHelper {

    private ProvidersHelper() {
    }

    public static <A> A getContext(Providers providers, Class<A> clazz) {
        return new ProvidersContext(providers).resolveNotNull(clazz);
    }
}
