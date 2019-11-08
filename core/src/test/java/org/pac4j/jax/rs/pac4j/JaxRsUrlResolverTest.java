package org.pac4j.jax.rs.pac4j;

import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.pac4j.core.context.WebContext;

public class JaxRsUrlResolverTest {

    @Test
    public void null_url_with_non_jaxrs_context_resolves_as_null() {
        WebContext context = mock(WebContext.class);
        
        JaxRsUrlResolver resolver = new JaxRsUrlResolver();
        String resolvedUrl = resolver.compute(null, context);
        assertThat(resolvedUrl, is(nullValue()));
    }

    @Test
    public void null_url_with_jaxrs_context_resolves_as_null() {
        JaxRsContext context = mock(JaxRsContext.class);
        
        JaxRsUrlResolver resolver = new JaxRsUrlResolver();
        String resolvedUrl = resolver.compute(null, context);
        assertThat(resolvedUrl, is(nullValue()));
    }

    @Test
    public void relative_url_with_non_jaxrs_context_is_left_unresolved() {
        WebContext context = mock(WebContext.class);
        
        JaxRsUrlResolver resolver = new JaxRsUrlResolver();
        String resolvedUrl = resolver.compute("/a/relative/url", context);
        assertThat(resolvedUrl, is("/a/relative/url"));
    }

    @Test
    public void relative_url_with_jaxrs_context_is_resolved_as_absolute_url() {
        JaxRsContext context = mock(JaxRsContext.class);
        when(context.getAbsolutePath(anyString(), eq(true))).thenAnswer(invocation -> { return "http://domain/app"+invocation.getArgument(0); });
        
        JaxRsUrlResolver resolver = new JaxRsUrlResolver();
        String resolvedUrl = resolver.compute("/a/relative/url", context);
        assertThat(resolvedUrl, is("http://domain/app/a/relative/url"));
    }

    @Test
    public void relative_url_with_null_context_is_left_unresolved() {
        JaxRsUrlResolver resolver = new JaxRsUrlResolver();
        String resolvedUrl = resolver.compute("/a/relative/url", null);
        assertThat(resolvedUrl, is("/a/relative/url"));
    }
}
