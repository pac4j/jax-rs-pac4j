package org.pac4j.jax.rs.filters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.MockJaxRs;

public class DefaultJaxRsHttpActionAdapterTest {

    private ContainerRequestContext requestContext;

    private JaxRsContext context;

    @Before
    public void setUp() {
        MockJaxRs.installRuntimeDelegate();
        requestContext = MockJaxRs.requestContext();
        context = new JaxRsContext(new RequestJaxRsContext(null, requestContext));
    }

    @After
    public void tearDown() {
        RuntimeDelegate.setInstance(null);
    }

    @Test
    public void request_is_aborted_by_default() {
        DefaultJaxRsHttpActionAdapter.INSTANCE.adapt(new UnauthorizedAction(), context);

        verify(requestContext).abortWith(any());
    }

    @Test
    public void request_is_aborted_when_response_is_not_skipped() {
        requestContext.setProperty(AbstractFilter.SKIP_RESPONSE_PROPERTY, false);

        DefaultJaxRsHttpActionAdapter.INSTANCE.adapt(new UnauthorizedAction(), context);

        verify(requestContext).abortWith(any());
    }

    @Test
    public void request_is_not_aborted_when_response_is_skipped() {
        requestContext.setProperty(AbstractFilter.SKIP_RESPONSE_PROPERTY, true);

        DefaultJaxRsHttpActionAdapter.INSTANCE.adapt(new UnauthorizedAction(), context);

        verify(requestContext, never()).abortWith(any());
    }

    @Test
    public void skip_response_is_false_unless_set_to_true() {
        SecurityFilter filter = new SecurityFilter(null);
        assertFalse(filter.isSkipResponse());

        filter.setSkipResponse(true);
        assertTrue(filter.isSkipResponse());
    }

    @Test(expected = TechnicalException.class)
    public void null_action_is_rejected() {
        DefaultJaxRsHttpActionAdapter.INSTANCE.adapt(null, context);
    }

    @Test(expected = TechnicalException.class)
    public void non_jax_rs_context_is_rejected() {
        DefaultJaxRsHttpActionAdapter.INSTANCE.adapt(new UnauthorizedAction(), mock(WebContext.class));
    }
}
