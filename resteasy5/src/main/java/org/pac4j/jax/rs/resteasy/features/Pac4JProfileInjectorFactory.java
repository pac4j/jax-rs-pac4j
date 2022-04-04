package org.pac4j.jax.rs.resteasy.features;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.helpers.RequestPac4JSecurityContext;
import org.pac4j.jax.rs.helpers.RequestProfileManager;
import org.pac4j.jax.rs.helpers.RequestUserProfile;
import org.pac4j.jax.rs.resteasy.helpers.RestEasyRequestContext;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for injecting the pac4j-profile into resteasy
 */
public class Pac4JProfileInjectorFactory extends InjectorFactoryImpl {

    private static Logger LOG = LoggerFactory.getLogger(Pac4JProfileInjectorFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        final ValueInjector injector = getValueInjector(parameter.getType(), parameter.getAnnotations(),
                providerFactory);
        if (injector != null)
            return injector;
        return super.createParameterExtractor(parameter, providerFactory);
    }

    /**
     * Gets the value-injector
     * 
     * @param type            the class of the field to inject
     * @param annotations     the annotations of the field
     * @param providerFactory the provider-facotry
     * @return the value-injector
     */
    private ValueInjector getValueInjector(Class<?> type, Annotation[] annotations,
            ResteasyProviderFactory providerFactory) {
        if (FindAnnotation.findAnnotation(annotations, Pac4JProfile.class) != null) {
            if (type.equals(Optional.class)) {
                return new Pac4JValueInjector(providerFactory, rc -> rc.context()
                        .flatMap(c -> new RequestUserProfile(new RequestPac4JSecurityContext(c)).profile()));
            } else {
                return new Pac4JValueInjector(providerFactory,
                        rc -> rc.context()
                                .flatMap(c -> new RequestUserProfile(new RequestPac4JSecurityContext(c)).profile())
                                .orElseThrow(() -> {
                                    LOG.debug(
                                            "Cannot inject a Pac4j profile into an unauthenticated request, responding with 401");
                                    return new WebApplicationException(401);
                                }));
            }
        } else if (FindAnnotation.findAnnotation(annotations, Pac4JProfileManager.class) != null) {
            return new Pac4JValueInjector(providerFactory,
                    c -> new RequestProfileManager(c.contextOrNew(), ServletSessionStore.INSTANCE).profileManager());
        } else {
            return null;
        }
    }

    /**
     * Pac4j-aware value-injector
     */
    public static class Pac4JValueInjector implements ValueInjector {
        private final Function<RequestJaxRsContext, Object> provider;
        private final ResteasyProviderFactory providerFactory;

        /**
         * Constructor
         * 
         * @param providerFactory the provider-factory
         * @param provider        the provider
         */
        Pac4JValueInjector(ResteasyProviderFactory providerFactory, Function<RequestJaxRsContext, Object> provider) {
            this.providerFactory = providerFactory;
            this.provider = provider;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object inject(boolean unwrapAsync) {
            return provider.apply(new RestEasyRequestContext(providerFactory));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
            return provider.apply(new RestEasyRequestContext(providerFactory, request));
        }
    }
}
