package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.core.context.Pac4jConstants;
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
     * @return value for {@link ApplicationLogoutFilter#setDefaultUrl(String)} (empty string means no redirect: instead
     *         the annotated method will be executed).
     */
    String value() default "";

    /**
     * @return value for {@link ApplicationLogoutFilter#setLogoutUrlPattern(String)}
     */
    String logoutUrlPattern() default Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
}
