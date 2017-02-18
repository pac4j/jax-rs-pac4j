package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.jax.rs.filters.LogoutFilter;

/**
 * Identify the class or method as being filtered by {@link LogoutFilter}.
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
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (false), one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     * 
     * @return value for {@link LogoutFilter#setSkipResponse(Boolean)}.
     */
    boolean[] skipResponse() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, a non-empty string will be used by the filter, and more than one string will fail the resource method
     * initialisation.
     * 
     * @return value for {@link LogoutFilter#setDefaultUrl(String)}.
     */
    String[] defaultUrl() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, a non-empty string will be used by the filter, and more than one string will fail the resource method
     * initialisation.
     * 
     * @return value for {@link LogoutFilter#setLogoutUrlPattern(String)}
     */
    String[] logoutUrlPattern() default {};
    
    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (false), one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     *
     * @return value for {@link LogoutFilter#setLocalLogout(Boolean)}
     */
    boolean[] localLogout() default {};
    
    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (false), one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     *
     * @return value for {@link LogoutFilter#setDestroySession(Boolean)}
     */
    boolean[] destroySession() default {};
    
    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (false), one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     *
     * @return value for {@link LogoutFilter#setCentralLogout(Boolean)}
     */
    boolean[] centralLogout() default {};
}
