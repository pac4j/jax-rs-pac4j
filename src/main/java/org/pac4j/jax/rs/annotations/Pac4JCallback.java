package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.jax.rs.filters.CallbackFilter;

/**
 * 
 * Identify the class or method as being filtered by {@link CallbackFilter}.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pac4JCallback {

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (no default client), one string will be used by the filter, and more than one string will fail the
     * resource method initialisation.
     * 
     * @return value for {@link CallbackFilter#setDefaultClient(String)}
     */
    String[] defaultClient() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting (false), one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     * 
     * @return value for {@link CallbackFilter#setSkipResponse(Boolean)}
     */
    boolean[] skipResponse() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, one empty string means that pac4j answer, such as redirect, will be skipped (the annotated method will
     * be executed), a non-empty string will be used by the filter, and more than one string will fail the resource
     * method initialisation.
     * 
     * @return value for {@link CallbackFilter#setDefaultUrl(String)}
     */
    String[] defaultUrl() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     * 
     * @return value for {@link CallbackFilter#setRenewSession(Boolean)}
     */
    boolean[] renewSession() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     * 
     * @return value for {@link CallbackFilter#setMultiProfile(Boolean)}
     */
    boolean[] multiProfile() default {};
}
