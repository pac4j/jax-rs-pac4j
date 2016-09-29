package org.pac4j.jax.rs.features.jersey;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Pac4JProfile &#64;Profile} injection value factory provider.
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

        private final Config config;

        @Inject
        protected Pac4JProfileValueFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator,
                ConfigProvider configProvider) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
            this.config = configProvider.config;
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
                return new ProfileValueFactory(config, parameter);
            }
        }
    }

    static class Pac4JProfileManagerValueFactoryProvider extends AbstractValueFactoryProvider {

        private final Config config;

        @Inject
        protected Pac4JProfileManagerValueFactoryProvider(MultivaluedParameterExtractorProvider mpep,
                ServiceLocator locator, ConfigProvider configProvider) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
            this.config = configProvider.config;
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
                return new ProfileManagerValueFactory(config);
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

    @Singleton
    static class ConfigProvider {

        private final Config config;

        ConfigProvider(Config config) {
            this.config = config;
        }
    }

    public static class Binder extends AbstractBinder {

        private final Config config;

        public Binder(Config config) {
            this.config = config;
        }

        @Override
        protected void configure() {
            bind(new ConfigProvider(config)).to(ConfigProvider.class);
            bind(ProfileManagerValueFactory.class).to(ProfileManagerValueFactory.class);
            bind(Pac4JProfileManagerValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(Pac4JProfileValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(ProfileInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfile>>() {
            }).in(Singleton.class);
            bind(ProfileManagerInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfileManager>>() {
            }).in(Singleton.class);
        }
    }

    static class ProfileManagerValueFactory implements Factory<ProfileManager<CommonProfile>> {

        @Context
        private HttpServletRequest request;

        private final Config config;

        @Inject
        public ProfileManagerValueFactory(ConfigProvider config) {
            this.config = config.config;
        }

        public ProfileManagerValueFactory(Config config) {
            this.config = config;
        }

        @Override
        public ProfileManager<CommonProfile> provide() {
            // we don't need the response for this
            final J2EContext context = new J2EContext(request, null, config.getSessionStore());
            return new ProfileManager<>(context);
        }

        @Override
        public void dispose(ProfileManager<CommonProfile> instance) {
            // nothing
        }
    }

    static class ProfileValueFactory implements Factory<CommonProfile> {

        private static final Logger LOG = LoggerFactory.getLogger(ProfileValueFactory.class);

        @Context
        private HttpServletRequest request;

        private final Config config;

        private final Parameter parameter;

        public ProfileValueFactory(Config config, Parameter parameter) {
            this.config = config;
            this.parameter = parameter;
        }

        @Override
        public CommonProfile provide() {
            // we don't need the response for this
            final J2EContext context = new J2EContext(request, null, config.getSessionStore());
            final Optional<CommonProfile> profile = new ProfileManager<>(context)
                    .get(parameter.getAnnotation(Pac4JProfile.class).readFromSession());
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
}
