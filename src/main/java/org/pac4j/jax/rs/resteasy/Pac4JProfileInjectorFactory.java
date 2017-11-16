package org.pac4j.jax.rs.resteasy;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;

@Provider
public class Pac4JProfileInjectorFactory extends InjectorFactoryImpl {

	@Override
	public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
												  Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
		final ValueInjector injector = getValueInjector(type, annotations);
		if (injector != null) return injector;
		return super.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, factory);
	}

	@Override
	public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
		final ValueInjector injector = getValueInjector(parameter.getType(), parameter.getAnnotations());
		if (injector != null) return injector;
		return super.createParameterExtractor(parameter, providerFactory);
	}

	private ValueInjector getValueInjector(Class type, Annotation[] annotations) {
		final Pac4JProfile profile;
		if ((profile = FindAnnotation.findAnnotation(annotations, Pac4JProfile.class)) != null) {
			if (type.equals(Optional.class)) {
				return new Pac4JValueInjector(
						(req, resp) -> new ProfileManager(new J2EContext(req, resp)).get(profile.readFromSession())
				);
			} else {
				return new Pac4JValueInjector(
						(req, resp) -> new ProfileManager(new J2EContext(req, resp)).get(profile.readFromSession()).orElse(null)
				);
			}
		} else if (FindAnnotation.findAnnotation(annotations, Pac4JProfileManager.class) != null)  {
			return new Pac4JValueInjector((req, resp) -> new ProfileManager(new J2EContext(req, resp)));
		} else {
			return null;
		}
	}

	public static class Pac4JValueInjector implements ValueInjector {
		private final BiFunction<HttpServletRequest, HttpServletResponse, Object> provider;

		Pac4JValueInjector(BiFunction<HttpServletRequest, HttpServletResponse, Object> provider) {
			this.provider = provider;
		}

		public Object inject(HttpRequest request, HttpResponse response) {
			return inject();
		}

		public Object inject() {
			final HttpServletRequest req = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
			final HttpServletResponse resp = ResteasyProviderFactory.getContextData(HttpServletResponse.class);
			return provider.apply(req, resp);
		}
	}

}
