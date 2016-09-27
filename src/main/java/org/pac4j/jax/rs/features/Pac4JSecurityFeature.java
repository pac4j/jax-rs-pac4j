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

import org.apache.commons.lang3.StringUtils;
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
 * TODO For now we need to also implement {@link Feature} because of https://java.net/jira/browse/JERSEY-3166. TODO
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

            final SecurityFilter filter = new SecurityFilter(request, config);

            if (secAnn.authorizers().length > 0) {
                filter.setAuthorizers(StringUtils.join(secAnn.authorizers(), ","));
            }

            if (secAnn.clients().length > 0) {
                filter.setClients(StringUtils.join(secAnn.clients(), ","));
            }

            if (secAnn.matchers().length > 0) {
                filter.setMatchers(StringUtils.join(secAnn.matchers(), ","));
            }

            filter.setMultiProfile(secAnn.multiProfile());

            context.register(filter);
        }

        final Pac4JCallback cbAnn = method.getAnnotation(Pac4JCallback.class);

        if (cbAnn != null) {
            final CallbackFilter filter = new CallbackFilter(request, config);

            filter.setMultiProfile(cbAnn.multiProfile());
            filter.setRenewSession(cbAnn.renewSession());
            filter.setDefaultUrl(cbAnn.defaultUrl());

            context.register(filter);
        }

        final Pac4JLogout lAnn = method.getAnnotation(Pac4JLogout.class);

        if (lAnn != null) {
            final ApplicationLogoutFilter filter = new ApplicationLogoutFilter(request, config);

            filter.setDefaultUrl(lAnn.defaultUrl());
            filter.setLogoutUrlPattern(lAnn.logoutUrlPattern());

            context.register(filter);
        }
    }

    @Override
    public boolean configure(FeatureContext context) {
        // nothing to do, it is here only to trigger injection of the @Context fields.
        return true;
    }
}