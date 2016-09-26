package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.features.jersey.Pac4JProfileValueFactoryProvider;

/**
 * 
 * Binds the value(s) of the current Pac4J {@link CommonProfile} to a resource method parameter, resource class field,
 * or resource class bean property.
 * 
 * @see Pac4JProfileValueFactoryProvider.Binder
 * @author Victor Noel - Linagora
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pac4JProfile {
}
