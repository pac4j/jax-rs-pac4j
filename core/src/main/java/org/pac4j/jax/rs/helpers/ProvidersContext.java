package org.pac4j.jax.rs.helpers;

import java.util.Optional;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class ProvidersContext {

    private final Providers providers;

    public ProvidersContext(Providers providers) {
        this.providers = providers;
    }

    public <A> Optional<A> resolve(Class<A> clazz) {
        ContextResolver<A> cr = providers.getContextResolver(clazz, MediaType.WILDCARD_TYPE);
        CommonHelper.assertNotNull("ContextResolver<" + clazz.getSimpleName() + ">", cr);
        return Optional.ofNullable(cr.getContext(null));
    }

    public <A> A resolveNotNull(Class<A> clazz) {
        return resolve(clazz).orElseThrow(() -> new TechnicalException(clazz.getName() + " cannot be null"));
    }
}
