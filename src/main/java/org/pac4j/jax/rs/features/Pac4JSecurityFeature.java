package org.pac4j.jax.rs.features;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.config.Config;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.filter.ApplicationLogoutFilter;
import org.pac4j.jax.rs.filter.CallbackFilter;
import org.pac4j.jax.rs.filter.SecurityFilter;

/**
 * 
 * TODO For now we need to also implement {@link Feature} because of https://java.net/jira/browse/JERSEY-3166.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Provider
public class Pac4JSecurityFeature implements DynamicFeature, Feature {

    /**
     * Note: this is a proxy that is injected and it will map to the correct request during filtering
     * 
     * TODO Normally we would want to inject that directly in one of the {@link ContainerRequestFilter}, but
     * https://java.net/jira/browse/JERSEY-3167 prevents this because we can't make them implement {@link Feature}.
     */
    @Context
    private HttpServletRequest request;
    
    private final Config config;

    public Pac4JSecurityFeature(Config config) {
        this.config = config;
    }

    /**
     * TODO support class-level annotations and overrides
     */
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        assert resourceInfo != null;
        assert context != null;

        CommonHelper.assertNotNull("request", request);

        final Method method = resourceInfo.getResourceMethod();

        final Pac4JSecurity secAnn = method.getAnnotation(Pac4JSecurity.class);

        if (secAnn != null) {

            if (secAnn.multiProfile().length > 1) {
                throw new IllegalArgumentException(
                        "multiProfile parameter in @Pac4JSecurity is not expected to have more than one value");
            }

            if (secAnn.skipResponse().length > 1) {
                throw new IllegalArgumentException(
                        "skipResponse parameter in @Pac4JSecurity is not expected to have more than one value");
            }

            final SecurityFilter filter = new SecurityFilter(request, config);

            filter.setAuthorizers(String.join(",", secAnn.authorizers()));
            filter.setClients(String.join(",", secAnn.clients()));
            filter.setMatchers(String.join(",", secAnn.matchers()));
            filter.setMultiProfile(secAnn.multiProfile().length == 0 ? null : secAnn.multiProfile()[0]);
            filter.setSkipResponse(secAnn.skipResponse().length == 0 ? null : secAnn.skipResponse()[0]);

            context.register(filter);
        }

        final Pac4JCallback cbAnn = method.getAnnotation(Pac4JCallback.class);

        if (cbAnn != null) {

            if (cbAnn.defaultUrl().length > 1) {
                throw new IllegalArgumentException(
                        "defaultUrl parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.multiProfile().length > 1) {
                throw new IllegalArgumentException(
                        "multiProfile parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.renewSession().length > 1) {
                throw new IllegalArgumentException(
                        "renewSession parameter in @Pac4JCallback is not expected to have more than one value");
            }

            if (cbAnn.skipResponse().length > 1) {
                throw new IllegalArgumentException(
                        "skipResponse parameter in @Pac4JCallback is not expected to have more than one value");
            }

            final CallbackFilter filter = new CallbackFilter(request, config);

            filter.setMultiProfile(cbAnn.multiProfile().length == 0 ? null : cbAnn.multiProfile()[0]);
            filter.setRenewSession(cbAnn.renewSession().length == 0 ? null : cbAnn.renewSession()[0]);
            filter.setDefaultUrl(cbAnn.defaultUrl().length == 0 ? null : cbAnn.defaultUrl()[0]);
            filter.setSkipResponse(cbAnn.skipResponse().length == 0 ? null : cbAnn.skipResponse()[0]);

            context.register(filter);
        }

        final Pac4JLogout lAnn = method.getAnnotation(Pac4JLogout.class);

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

            final ApplicationLogoutFilter filter = new ApplicationLogoutFilter(request, config);

            filter.setDefaultUrl(lAnn.defaultUrl().length == 0 ? null : lAnn.defaultUrl()[0]);
            filter.setLogoutUrlPattern(lAnn.logoutUrlPattern().length == 0 ? null : lAnn.logoutUrlPattern()[0]);
            filter.setSkipResponse(lAnn.skipResponse().length == 0 ? null : lAnn.skipResponse()[0]);

            context.register(filter);
        }
    }

    @Override
    public boolean configure(FeatureContext context) {
        // nothing to do, it is here only to trigger injection of the @Context fields.
        return true;
    }
}