package org.pac4j.jax.rs.jersey.features;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
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
import org.pac4j.jax.rs.helpers.ProvidersHelper;
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
public class Pac4JValueFactoryProvider {

    static class Pac4JProfileValueFactoryProvider extends AbstractValueFactoryProvider {

        private final OptionalProfileFactoryBuilder optProfile;
        private final ProfileFactoryBuilder profile;

        @Inject
        protected Pac4JProfileValueFactoryProvider(OptionalProfileFactoryBuilder opt, ProfileFactoryBuilder profile,
                MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
            this.optProfile = opt;
            this.profile = profile;
        }

        @Override
        protected Factory<?> createValueFactory(Parameter parameter) {
            assert parameter != null;

            if (!parameter.isAnnotationPresent(Pac4JProfile.class)) {
                return null;
            }

            if (CommonProfile.class.isAssignableFrom(parameter.getRawType())) {
                return profile.apply(parameter);
            }

            if (Optional.class.isAssignableFrom(parameter.getRawType())) {
                List<ClassTypePair> ctps = ReflectionHelper.getTypeArgumentAndClass(parameter.getRawType());
                ClassTypePair ctp = (ctps.size() == 1) ? ctps.get(0) : null;
                if (ctp == null || CommonProfile.class.isAssignableFrom(ctp.rawClass())) {
                    return optProfile.apply(parameter);
                }
            }

            throw new IllegalStateException(
                    "Cannot inject a Pac4J profile into a parameter of type " + parameter.getRawType().getName());
        }
    }

    static class Pac4JProfileManagerValueFactoryProvider extends AbstractValueFactoryProvider {

        private final ProfileManagerFactoryBuilder manager;

        @Inject
        protected Pac4JProfileManagerValueFactoryProvider(ProfileManagerFactoryBuilder manager,
                MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
            this.manager = manager;
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
                return manager.apply(parameter);
            }
        }
    }

    static class ProfileManagerInjectionResolver extends ParamInjectionResolver<Pac4JProfileManager> {
        ProfileManagerInjectionResolver() {
            super(Pac4JProfileManagerValueFactoryProvider.class);
        }
    }

    static class ProfileInjectionResolver extends ParamInjectionResolver<Pac4JProfile> {
        ProfileInjectionResolver() {
            super(Pac4JProfileValueFactoryProvider.class);
        }
    }

    public interface OptionalProfileFactory extends Factory<Optional<CommonProfile>> {
        @Override
        default void dispose(Optional<CommonProfile> instance) {
            // do nothing
        }
    }

    public interface ProfileFactory extends Factory<CommonProfile> {
        @Override
        default void dispose(CommonProfile instance) {
            // do nothing
        }
    }

    public interface ProfileManagerFactory extends Factory<ProfileManager<CommonProfile>> {
        @Override
        default void dispose(ProfileManager<CommonProfile> instance) {
            // do nothing
        }
    }

    public interface OptionalProfileFactoryBuilder extends Function<Parameter, OptionalProfileFactory> {

    }

    public interface ProfileFactoryBuilder extends Function<Parameter, ProfileFactory> {

    }

    public interface ProfileManagerFactoryBuilder extends Function<Parameter, ProfileManagerFactory> {

    }

    public static class Binder extends AbstractBinder {

        private final ProfileFactoryBuilder profile;
        private final OptionalProfileFactoryBuilder optProfile;
        private final ProfileManagerFactoryBuilder manager;

        /**
         * Use this in your applications
         */
        public Binder() {
            this(null, null, null);
        }

        /**
         * Use this if you want to mock the {@link CommonProfile} or the {@link ProfileManager}.
         * 
         * @param profile
         *            a builder for a {@link CommonProfile}, can be <code>null</code> and default will be used.
         * @param optProfile
         *            a builder for an {@link Optional} of {@link CommonProfile}, can be <code>null</code> and default
         *            will be used.
         * @param manager
         *            a builder for a {@link ProfileManager}, can be <code>null</code> and default will be used.
         */
        public Binder(ProfileFactoryBuilder profile, OptionalProfileFactoryBuilder optProfile,
                ProfileManagerFactoryBuilder manager) {
            this.profile = profile == null ? ProfileValueFactory::new : profile;
            this.optProfile = optProfile == null ? OptionalProfileValueFactory::new : optProfile;
            this.manager = manager == null ? p -> new ProfileManagerValueFactory() : manager;
        }

        /**
         * Use this if you want to always return the same {@link CommonProfile} (or none with <code>null</code>).
         * 
         * Note that it won't mock the profile coming out of {@link ProfileManager}!
         * 
         * @param profile
         *            a profile, can be <code>null</code>.
         */
        public Binder(CommonProfile profile) {
            this(p -> () -> profile, p -> () -> Optional.ofNullable(profile), null);
        }

        @Override
        protected void configure() {
            bind(profile).to(ProfileFactoryBuilder.class);
            bind(optProfile).to(OptionalProfileFactoryBuilder.class);
            bind(manager).to(ProfileManagerFactoryBuilder.class);

            bind(Pac4JProfileManagerValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(Pac4JProfileValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);

            bind(ProfileInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfile>>() {
            }).in(Singleton.class);
            bind(ProfileManagerInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Pac4JProfileManager>>() {
            }).in(Singleton.class);
        }
    }

    static class ProfileManagerValueFactory extends AbstractJaxRsContextValueFactory<ProfileManager<CommonProfile>>
            implements ProfileManagerFactory {
        @Override
        public ProfileManager<CommonProfile> provide() {
            return new ProfileManager<>(getContext());
        }
    }

    static class ProfileValueFactory extends AbstractJaxRsContextValueFactory<CommonProfile> implements ProfileFactory {

        private static Logger LOG = LoggerFactory.getLogger(ProfileValueFactory.class);

        @Context
        private Providers providers;

        private final Parameter parameter;

        public ProfileValueFactory(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public CommonProfile provide() {
            final boolean readFromSession = parameter.getAnnotation(Pac4JProfile.class).readFromSession();
            final Optional<CommonProfile> profile = new ProfileManager<>(getContext()).get(readFromSession);

            if (profile.isPresent()) {
                return profile.get();
            }

            LOG.debug("Cannot inject a Pac4j profile into an unauthenticated request, responding with 401");

            throw new WebApplicationException(401);
        }
    }

    static class OptionalProfileValueFactory extends AbstractJaxRsContextValueFactory<Optional<CommonProfile>>
            implements OptionalProfileFactory {

        @Context
        private Providers providers;

        private final Parameter parameter;

        public OptionalProfileValueFactory(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public Optional<CommonProfile> provide() {
            final boolean readFromSession = parameter.getAnnotation(Pac4JProfile.class).readFromSession();
            final Optional<CommonProfile> profile = new ProfileManager<>(getContext()).get(readFromSession);

            return profile;
        }
    }

    static abstract class AbstractJaxRsContextValueFactory<T> extends AbstractContainerRequestValueFactory<T> {

        @Context
        private Providers providers;

        protected JaxRsContext getContext() {
            JaxRsContext context = ProvidersHelper.getContext(providers, JaxRsContextFactory.class)
                    .provides(getContainerRequest());
            assert context != null;
            return context;
        }
    }
}
