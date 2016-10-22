package org.pac4j.jax.rs.jersey.features;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Pac4JProfile &#64;Pac4JProfile} injection value factory provider.
 * 
 * Register a new {@link Binder} in order to enable this.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 * 
 */
@Singleton
public class Pac4JValueFactoryProvider {

    static class Pac4JProfileValueFactoryProvider extends AbstractValueFactoryProvider {

        @Inject
        protected Pac4JProfileValueFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
        }

        @Override
        protected Factory<?> createValueFactory(Parameter parameter) {
            assert parameter != null;
            if (!parameter.isAnnotationPresent(Pac4JProfile.class)) {
                return null;
            } else if (!CommonProfile.class.isAssignableFrom(parameter.getRawType())) {
                throw new IllegalStateException(
                        "Cannot inject a Pac4J profile into a parameter of type " + parameter.getRawType().getName());
            } else {
                return new ProfileValueFactory(parameter);
            }
        }
    }

    static class Pac4JProfileManagerValueFactoryProvider extends AbstractValueFactoryProvider {

        @Inject
        protected Pac4JProfileManagerValueFactoryProvider(MultivaluedParameterExtractorProvider mpep,
                ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
        }

        @Override
        protected Factory<?> createValueFactory(Parameter parameter) {
            assert parameter != null;
            if (!parameter.isAnnotationPresent(Pac4JProfileManager.class)) {
                return null;
            } else if (!ProfileManager.class.isAssignableFrom(parameter.getRawType())) {
                throw new IllegalStateException("Cannot inject a Pac4J profile manager into a parameter of type "
                        + parameter.getRawType().getName());
            } else {
                return new ProfileManagerValueFactory();
            }
        }
    }

    @Singleton
    static class ProfileManagerInjectionResolver extends ParamInjectionResolver<Pac4JProfileManager> {
        ProfileManagerInjectionResolver() {
            super(Pac4JProfileManagerValueFactoryProvider.class);
        }
    }

    @Singleton
    static class ProfileInjectionResolver extends ParamInjectionResolver<Pac4JProfile> {
        ProfileInjectionResolver() {
            super(Pac4JProfileValueFactoryProvider.class);
        }
    }

    public static class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(Pac4JProfileManagerValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(Pac4JProfileValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(ProfileInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfile>>() {
            }).in(Singleton.class);
            bind(ProfileManagerInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfileManager>>() {
            }).in(Singleton.class);
        }
    }

    static class ProfileManagerValueFactory extends AbstractJaxRsContextValueFactory<ProfileManager<CommonProfile>> {

        @Override
        public ProfileManager<CommonProfile> provide() {
            return new ProfileManager<>(getJaxRsContext());
        }

        @Override
        public void dispose(ProfileManager<CommonProfile> instance) {
            // nothing
        }
    }

    static class ProfileValueFactory extends AbstractJaxRsContextValueFactory<CommonProfile> {

        private static final Logger LOG = LoggerFactory.getLogger(ProfileValueFactory.class);

        @Context
        private Providers providers;

        private final Parameter parameter;

        public ProfileValueFactory(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public CommonProfile provide() {
            final boolean readFromSession = parameter.getAnnotation(Pac4JProfile.class).readFromSession();
            final Optional<CommonProfile> profile = new ProfileManager<>(getJaxRsContext()).get(readFromSession);
            if (profile.isPresent()) {
                final CommonProfile p = profile.get();
                if (parameter.getRawType().isInstance(p)) {
                    return p;
                } else {
                    // this is most certainly a programmer error
                    LOG.warn("Cannot inject a Pac4J profile of type {} into a parameter of type {}",
                            p.getClass().getName(), parameter.getRawType().getName());
                    return null;
                }
            } else {
                // there could be reason for that (after a callback for example)
                LOG.debug("Cannot inject a Pac4J profile into an unauthenticated request");
                return null;
            }
        }

        @Override
        public void dispose(CommonProfile instance) {
            // nothing
        }
    }
    
    static abstract class AbstractJaxRsContextValueFactory<T> extends AbstractContainerRequestValueFactory<T> {

        @Context
        private Providers providers;

        protected JaxRsContext getJaxRsContext() {
            ContextResolver<JaxRsContextFactory> contextResolver = providers
                    .getContextResolver(JaxRsContextFactory.class, MediaType.WILDCARD_TYPE);
            JaxRsContextFactory contextFactory = contextResolver.getContext(JaxRsContextFactory.class);
            JaxRsContext context = contextFactory.provides(getContainerRequest());
            assert context != null;
            return context;
        }
    }
}
