package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.core.profile.ProfileManager;

/**
 * 
 * Binds the value(s) of the current Pac4J {@link ProfileManager} to a resource method parameter, resource class field,
 * or resource class bean property.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pac4JProfileManager {
}
