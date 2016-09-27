package org.pac4j.jax.rs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pac4j.jax.rs.filter.CallbackFilter;

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
     * @return value for {@link CallbackFilter#setDefaultUrl(String)} (empty string means no redirect: instead the
     *         annotated method will be executed).
     */
    String value() default "";

    /**
     * @return value for {@link CallbackFilter#setRenewSession(boolean)}
     */
    boolean renewSession() default false;

    /**
     * @return value for {@link CallbackFilter#setMultiProfile(boolean)}
     */
    boolean multiProfile() default false;
}
