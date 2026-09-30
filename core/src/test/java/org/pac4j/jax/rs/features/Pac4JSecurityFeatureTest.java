package org.pac4j.jax.rs.features;

import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

import org.junit.Test;
import org.pac4j.jax.rs.annotations.Pac4JLogout;

public class Pac4JSecurityFeatureTest {

    public static class Resource {

        @Pac4JLogout(localLogout = { true, false })
        public void localLogout() {
        }

        @Pac4JLogout(destroySession = { true, false })
        public void destroySession() {
        }

        @Pac4JLogout(centralLogout = { true, false })
        public void centralLogout() {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void several_local_logout_values_are_rejected() throws Exception {
        configure("localLogout");
    }

    @Test(expected = IllegalArgumentException.class)
    public void several_destroy_session_values_are_rejected() throws Exception {
        configure("destroySession");
    }

    @Test(expected = IllegalArgumentException.class)
    public void several_central_logout_values_are_rejected() throws Exception {
        configure("centralLogout");
    }

    private void configure(String methodName) throws NoSuchMethodException {
        Method method = Resource.class.getMethod(methodName);
        ResourceInfo resourceInfo = mock(ResourceInfo.class);
        doReturn(Resource.class).when(resourceInfo).getResourceClass();
        when(resourceInfo.getResourceMethod()).thenReturn(method);

        new Pac4JSecurityFeature().configure(resourceInfo, mock(FeatureContext.class));
    }
}
