package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.jax.rs.filter.ApplicationLogoutFilter;

/**
 * Identify the class or method as being filtered by {@link ApplicationLogoutFilter}.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pac4JLogout {

    /**
     * Default value is <code>false</code>.
     * 
     * If set to <code>true</code>, the pac4j response, such as redirect, will be skipped (the annotated method will be
     * executed instead).
     * 
     * @return value for {@link ApplicationLogoutFilter#setSkipResponse(boolean)}.
     */
    boolean skipResponse() default true;

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, a non-empty string will be used by the filter, and more than one string will fail the resource method
     * initialisation.
     * 
     * @return value for {@link ApplicationLogoutFilter#setDefaultUrl(String)}.
     */
    String[] defaultUrl() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, a non-empty string will be used by the filter, and more than one string will fail the resource method
     * initialisation.
     * 
     * @return value for {@link ApplicationLogoutFilter#setLogoutUrlPattern(String)}
     */
    String[] logoutUrlPattern() default {};
}
