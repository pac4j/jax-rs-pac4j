package org.pac4j.jax.rs.features;

import java.lang.reflect.Method;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.filters.CallbackFilter;
import org.pac4j.jax.rs.filters.LogoutFilter;
import org.pac4j.jax.rs.filters.SecurityFilter;
import org.pac4j.jax.rs.helpers.AnnotationsHelper;

/**
 *
 * Injects {@link SecurityFilter}s, {@link CallbackFilter}s and {@link LogoutFilter}s on JAX-RS resources methods
 * annotated with {@link Pac4JSecurity &#64;Pac4JSecurity}, {@link Pac4JCallback &#64;Pac4JCallback} and
 * {@link Pac4JLogout &#64;Pac4JLogout}.
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class Pac4JSecurityFeature implements DynamicFeature, Feature {

    /**
     * TODO Normally we would want to inject this directly in one of the {@link ContainerRequestFilter}, but
     * https://java.net/jira/browse/JERSEY-3167 prevents this because we can't make them implement {@link Feature}.
     */
    @Context
    private Providers providers;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        assert resourceInfo != null;
        assert context != null;

        final Class<?> clazz = resourceInfo.getResourceClass();
        final Pac4JSecurity classSecAnn = clazz == null ? null
                : AnnotationsHelper.getClassLevelAnnotation(clazz, Pac4JSecurity.class);

        final Method method = resourceInfo.getResourceMethod();
        final Pac4JSecurity methSecAnn = method == null ? null
                : AnnotationsHelper.getMethodLevelAnnotation(method, Pac4JSecurity.class);

        final Pac4JSecurity secAnn;
        // method annotation simply overrides classes
        if (methSecAnn != null) {
            secAnn = methSecAnn;
        } else {
            secAnn = classSecAnn;
        }

        if (secAnn != null && !secAnn.ignore()) {

            if (secAnn.skipResponse().length > 1) {
                throw new IllegalArgumentException(
                        "skipResponse parameter in @Pac4JSecurity is not expected to have more than one value");
            }

            final SecurityFilter filter = new SecurityFilter(providers);

            // if there is no clients specified, it is not the same as ""
            // no clients will exploit JaxRsConfig.getDefaultClients()
            String clients;
            if (secAnn.clients().length == 0) {
                clients = null;
            } else {
                clients = String.join(",", secAnn.clients());
            }

            filter.setAuthorizers(String.join(",", secAnn.authorizers()));
            filter.setClients(clients);
            filter.setMatchers(String.join(",", secAnn.matchers()));
            filter.setSkipResponse(secAnn.skipResponse().length == 0 ? null : secAnn.skipResponse()[0]);

            context.register(filter);
        }

        final Pac4JCallback cbAnn = method == null ? null
                : AnnotationsHelper.getMethodLevelAnnotation(method, Pac4JCallback.class);

        if (cbAnn != null) {

            if (cbAnn.defaultUrl().length > 1) {
                throw new IllegalArgumentException(
                        "defaultUrl parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.renewSession().length > 1) {
                throw new IllegalArgumentException(
                        "renewSession parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.skipResponse().length > 1) {
                throw new IllegalArgumentException(
                        "skipResponse parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.defaultClient().length > 1) {
                throw new IllegalArgumentException(
                        "defaultClient parameter in @Pac4JCallback is not expected to have more than one value");
            }

            final CallbackFilter filter = new CallbackFilter(providers);

            filter.setRenewSession(cbAnn.renewSession().length == 0 ? null : cbAnn.renewSession()[0]);
            filter.setDefaultUrl(cbAnn.defaultUrl().length == 0 ? null : cbAnn.defaultUrl()[0]);
            filter.setSkipResponse(cbAnn.skipResponse().length == 0 ? null : cbAnn.skipResponse()[0]);
            filter.setDefaultClient(cbAnn.defaultClient().length == 0 ? null : cbAnn.defaultClient()[0]);

            context.register(filter);
        }

        final Pac4JLogout lAnn = method == null ? null
                : AnnotationsHelper.getMethodLevelAnnotation(method, Pac4JLogout.class);

        if (lAnn != null) {

            if (lAnn.defaultUrl().length > 1) {
                throw new IllegalArgumentException(
                        "defaultUrl parameter in @Pac4JLogout is not expected to have more than one value");
            }

            if (lAnn.logoutUrlPattern().length > 1) {
                throw new IllegalArgumentException(
                        "logoutUrlPattern parameter in @Pac4JLogout is not expected to have more than one value");
            }

            if (lAnn.skipResponse().length > 1) {
                throw new IllegalArgumentException(
                        "skipResponse parameter in @Pac4JLogout is not expected to have more than one value");
            }

            final LogoutFilter filter = new LogoutFilter(providers);

            filter.setDefaultUrl(lAnn.defaultUrl().length == 0 ? null : lAnn.defaultUrl()[0]);
            filter.setLogoutUrlPattern(lAnn.logoutUrlPattern().length == 0 ? null : lAnn.logoutUrlPattern()[0]);
            filter.setSkipResponse(lAnn.skipResponse().length == 0 ? null : lAnn.skipResponse()[0]);
            filter.setLocalLogout(lAnn.localLogout().length == 0 ? null : lAnn.localLogout()[0]);
            filter.setDestroySession(lAnn.destroySession().length == 0 ? null : lAnn.destroySession()[0]);
            filter.setCentralLogout(lAnn.centralLogout().length == 0 ? null : lAnn.centralLogout()[0]);

            context.register(filter);
        }
    }

    @Override
    public boolean configure(FeatureContext context) {
        // nothing to do, it is here only to trigger injection of the @Context
        // fields.
        return true;
    }
}
