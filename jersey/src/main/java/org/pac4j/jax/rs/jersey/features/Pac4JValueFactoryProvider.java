package org.pac4j.jax.rs.jersey.features;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractValueParamProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.helpers.RequestCommonProfile;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.helpers.RequestPac4JSecurityContext;
import org.pac4j.jax.rs.helpers.RequestProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.ext.Providers;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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

    private static Logger LOG = LoggerFactory.getLogger(Pac4JValueFactoryProvider.class);

    static class Pac4JProfileValueFactoryProvider extends AbstractValueParamProvider {

        private final ProfileManagerFactoryBuilder manager;
        private final OptionalProfileFactoryBuilder optProfile;
        private final ProfileFactoryBuilder profile;

        @Inject
        protected Pac4JProfileValueFactoryProvider(
            ProfileManagerFactoryBuilder manager,
            OptionalProfileFactoryBuilder opt,
            ProfileFactoryBuilder profile,
            Provider<MultivaluedParameterExtractorProvider> mpep
        ) {
            super(mpep, Parameter.Source.UNKNOWN);
            this.manager = manager;
            this.optProfile = opt;
            this.profile = profile;
        }

        @Override
        protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
            if (parameter.isAnnotationPresent(Pac4JProfileManager.class)) {
                if (ProfileManager.class.isAssignableFrom(parameter.getRawType())) {
                    return manager.get();
                }

                throw new IllegalStateException("Cannot inject a Pac4J profile manager into a parameter of type "
                    + parameter.getRawType().getName());
            }

            if (parameter.isAnnotationPresent(Pac4JProfile.class)) {
                if (CommonProfile.class.isAssignableFrom(parameter.getRawType())) {
                    return profile.get();
                }

                if (Optional.class.isAssignableFrom(parameter.getRawType())) {
                    List<ClassTypePair> ctps = ReflectionHelper.getTypeArgumentAndClass(parameter.getRawType());
                    ClassTypePair ctp = (ctps.size() == 1) ? ctps.get(0) : null;
                    if (ctp == null || CommonProfile.class.isAssignableFrom(ctp.rawClass())) {
                        return optProfile.get();
                    }
                }

                throw new IllegalStateException(
                    "Cannot inject a Pac4J profile into a parameter of type " + parameter.getRawType().getName());
            }

            return null;
        }
    }

    static class ProfileManagerInjectionResolver extends ParamInjectionResolver<Pac4JProfileManager> {
        @Inject
        ProfileManagerInjectionResolver(
            Pac4JProfileValueFactoryProvider valueFactoryProvider,
            Provider<ContainerRequest> containerRequestProvider
        ) {
            super(
                valueFactoryProvider,
                Pac4JProfileManager.class,
                containerRequestProvider
            );
        }
    }

    static class ProfileInjectionResolver extends ParamInjectionResolver<Pac4JProfile> {
        @Inject
        ProfileInjectionResolver(
            Pac4JProfileValueFactoryProvider valueFactoryProvider,
            Provider<ContainerRequest> containerRequestProvider
        ) {
            super(
                valueFactoryProvider,
                Pac4JProfile.class,
                containerRequestProvider
            );
        }
    }

    public interface OptionalProfileFactory extends Function<ContainerRequest, Optional<CommonProfile>> {}
    public interface OptionalProfileFactoryBuilder extends Supplier<OptionalProfileFactory> {}

    public interface ProfileFactory extends Function<ContainerRequest, CommonProfile> {}
    public interface ProfileFactoryBuilder extends Supplier<ProfileFactory> {}

    public interface ProfileManagerFactory extends Function<ContainerRequest, ProfileManager<CommonProfile>> {}
    public interface ProfileManagerFactoryBuilder extends Supplier<ProfileManagerFactory> {}

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
            this.manager = manager;
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
            this(() -> (ignored) -> profile, () -> (ignored) -> Optional.ofNullable(profile), null);
        }

        /**
         * Use this if you want to return a dynamically supplied {@link CommonProfile} (or none with <code>null</code>).
         * 
         * Note that it won't mock the profile coming out of {@link ProfileManager}!
         * 
         * @param profile
         *            a profile supplier, can return <code>null</code>.
         */
        public Binder(Supplier<CommonProfile> profile) {
            this(() -> (ignored) -> profile.get(), () -> (ignored) -> Optional.ofNullable(profile.get()), null);
        }

        @Override
        protected void configure() {
            bind(profile).to(ProfileFactoryBuilder.class);
            bind(optProfile).to(OptionalProfileFactoryBuilder.class);

            if(manager == null){
                bind(DefaultProfileManagerFactoryBuilder.class)
                    .to(ProfileManagerFactoryBuilder.class)
                ;
            } else {
                bind(manager).to(ProfileManagerFactoryBuilder.class);
            }

            bind(Pac4JProfileValueFactoryProvider.class).to(ValueParamProvider.class).in(Singleton.class);

            bind(ProfileInjectionResolver.class)
                .to(new GenericType<InjectionResolver<Pac4JProfile>>(){})
                .in(Singleton.class);

            bind(ProfileManagerInjectionResolver.class)
                .to(new GenericType<InjectionResolver<Pac4JProfileManager>>(){})
                .in(Singleton.class);
        }
    }

    static class ProfileManagerValueFactory implements ProfileManagerFactory{
        @Context
        private final Providers providers;

        ProfileManagerValueFactory(Providers providers) {
            this.providers = providers;
        }

        @Override
        public ProfileManager<CommonProfile> apply(ContainerRequest containerRequest) {
            return new RequestProfileManager(new RequestJaxRsContext(providers, containerRequest))
                .profileManager();
        }
    }

    static class ProfileValueFactory implements ProfileFactory {
        @Override
        public CommonProfile apply(ContainerRequest containerRequest) {
            return optionalProfile(containerRequest)
                .orElseThrow(() -> {
                    LOG.debug("Cannot inject a Pac4j profile into an unauthenticated request, responding with 401");
                    return new WebApplicationException(401);
                });
        }
    }

    static class OptionalProfileValueFactory implements OptionalProfileFactory {
        @Override
        public Optional<CommonProfile> apply(ContainerRequest containerRequest) {
            return optionalProfile(containerRequest);
        }
    }

    private static Optional<CommonProfile> optionalProfile(ContainerRequest containerRequest) {
        RequestPac4JSecurityContext securityContext = new RequestPac4JSecurityContext(containerRequest);
        return new RequestCommonProfile(securityContext).profile();
    }

    public static class DefaultProfileManagerFactoryBuilder implements ProfileManagerFactoryBuilder {
        @Context
        private Providers providers;

        @Override
        public ProfileManagerFactory get() {
            return new ProfileManagerValueFactory(providers);
        }
    }
}