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
     * @return value for {@link SecurityFilter#setMultiProfile(boolean)}
     */
    boolean multiProfile() default false;
}
