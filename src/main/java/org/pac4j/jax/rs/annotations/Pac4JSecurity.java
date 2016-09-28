package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.jax.rs.filter.SecurityFilter;

/**
 * 
 * Identify the class or method as being filtered by {@link SecurityFilter}.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pac4JSecurity {

    /**
     * Default value is <code>false</code>.
     * 
     * If set to <code>true</code>, the pac4j response, such as redirect, will be skipped (the annotated method will be
     * executed instead).
     * 
     * @return value for {@link SecurityFilter#setSkipResponse(boolean)}
     */
    boolean skipResponse() default false;

    /**
     * @return value for {@link SecurityFilter#setAuthorizers(String)}
     */
    String[] authorizers() default {};

    /**
     * @return value for {@link SecurityFilter#setClients(String)}
     */
    String[] clients() default {};

    /**
     * @return value for {@link SecurityFilter#setMatchers(String)}
     */
    String[] matchers() default {};

    /**
     * Note that this parameter only takes one value at most: empty array (default) is used to represent default pac4j
     * setting, one boolean will be used by the filter, and more than one boolean will fail the resource method
     * initialisation.
     * 
     * @return value for {@link SecurityFilter#setMultiProfile(Boolean)}
     */
    boolean[] multiProfile() default {};
}
