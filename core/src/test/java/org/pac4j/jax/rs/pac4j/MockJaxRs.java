package org.pac4j.jax.rs.pac4j;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;

/**
 * Mocks for testing without a JAX-RS runtime.
 */
public final class MockJaxRs {

    private MockJaxRs() {
    }

    /**
     * {@link jakarta.ws.rs.core.Response#ok()} needs a JAX-RS runtime, which core does not have. Reset it with
     * <code>RuntimeDelegate.setInstance(null)</code> after the test.
     */
    public static void installRuntimeDelegate() {
        RuntimeDelegate runtimeDelegate = mock(RuntimeDelegate.class);
        when(runtimeDelegate.createResponseBuilder()).thenAnswer(i -> mock(ResponseBuilder.class, RETURNS_SELF));
        RuntimeDelegate.setInstance(runtimeDelegate);
    }

    /**
     * @return a request context which keeps its properties
     */
    public static ContainerRequestContext requestContext() {
        Map<String, Object> properties = new HashMap<>();
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getProperty(anyString())).thenAnswer(i -> properties.get(i.<String>getArgument(0)));
        doAnswer(i -> properties.put(i.getArgument(0), i.getArgument(1))).when(requestContext)
                .setProperty(anyString(), any());
        return requestContext;
    }
}
