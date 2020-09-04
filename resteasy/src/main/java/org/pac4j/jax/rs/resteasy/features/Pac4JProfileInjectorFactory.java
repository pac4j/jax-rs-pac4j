package org.pac4j.jax.rs.resteasy.features;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.helpers.RequestUserProfile;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.helpers.RequestPac4JSecurityContext;
import org.pac4j.jax.rs.helpers.RequestProfileManager;
import org.pac4j.jax.rs.resteasy.helpers.RestEasyRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yegor Gemba
 * @since 2.1.0
 */
@Provider
public class Pac4JProfileInjectorFactory extends InjectorFactoryImpl {
    private static Logger LOG = LoggerFactory.getLogger(Pac4JProfileInjectorFactory.class);

    @Override
    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
            Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        final ValueInjector injector = getValueInjector(type, annotations, factory);
        if (injector != null)
            return injector;
        return super.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, factory);
    }

    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        final ValueInjector injector = getValueInjector(parameter.getType(), parameter.getAnnotations(),
                providerFactory);
        if (injector != null)
            return injector;
        return super.createParameterExtractor(parameter, providerFactory);
    }

    private ValueInjector getValueInjector(Class type, Annotation[] annotations,
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
            return new Pac4JValueInjector(providerFactory, c -> new RequestProfileManager(c).profileManager());
        } else {
            return null;
        }
    }

    public static class Pac4JValueInjector implements ValueInjector {
        private final Function<RequestJaxRsContext, Object> provider;
        private final ResteasyProviderFactory providerFactory;

        Pac4JValueInjector(ResteasyProviderFactory providerFactory, Function<RequestJaxRsContext, Object> provider) {
            this.providerFactory = providerFactory;
            this.provider = provider;
        }

        @Override
        public Object inject(HttpRequest request, HttpResponse response) {
            return provider.apply(new RestEasyRequestContext(providerFactory, request));
        }

        @Override
        public Object inject() {
            return provider.apply(new RestEasyRequestContext(providerFactory));
        }
    }
}
