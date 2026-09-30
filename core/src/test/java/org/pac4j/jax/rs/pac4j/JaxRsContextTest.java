package org.pac4j.jax.rs.pac4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.Cookie;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;

public class JaxRsContextTest {

    private JaxRsContext context;

    @Before
    public void setUp() {
        // Response.ok() needs a JAX-RS runtime, which core does not have
        RuntimeDelegate runtimeDelegate = mock(RuntimeDelegate.class);
        when(runtimeDelegate.createResponseBuilder()).thenAnswer(i -> mock(ResponseBuilder.class, RETURNS_SELF));
        RuntimeDelegate.setInstance(runtimeDelegate);

        Map<String, Object> properties = new HashMap<>();
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getProperty(anyString())).thenAnswer(i -> properties.get(i.<String>getArgument(0)));
        doAnswer(i -> properties.put(i.getArgument(0), i.getArgument(1))).when(requestContext)
                .setProperty(anyString(), any());

        context = new JaxRsContext(new RequestJaxRsContext(null, requestContext));
    }

    @After
    public void tearDown() {
        RuntimeDelegate.setInstance(null);
    }

    @Test
    public void response_cookie_keeps_all_its_attributes() {
        Cookie cookie = new Cookie("name", "value");
        cookie.setPath("/path");
        cookie.setDomain("example.com");
        cookie.setMaxAge(3600);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setSameSitePolicy("Strict");

        NewCookie newCookie = addAndGetResponseCookie(cookie);

        assertEquals("name", newCookie.getName());
        assertEquals("value", newCookie.getValue());
        assertEquals("/path", newCookie.getPath());
        assertEquals("example.com", newCookie.getDomain());
        assertEquals(3600, newCookie.getMaxAge());
        assertTrue(newCookie.isSecure());
        assertTrue(newCookie.isHttpOnly());
        assertEquals(NewCookie.SameSite.STRICT, newCookie.getSameSite());
    }

    @Test
    public void same_site_policy_is_case_insensitive() {
        Cookie cookie = new Cookie("name", "value");
        cookie.setSameSitePolicy("lax");

        assertEquals(NewCookie.SameSite.LAX, addAndGetResponseCookie(cookie).getSameSite());
    }

    @Test
    public void unknown_same_site_policy_is_ignored() {
        Cookie cookie = new Cookie("name", "value");
        cookie.setSameSitePolicy("unknown");

        assertNull(addAndGetResponseCookie(cookie).getSameSite());
    }

    private NewCookie addAndGetResponseCookie(Cookie cookie) {
        context.addResponseCookie(cookie);

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        when(responseContext.getHeaders()).thenReturn(headers);
        context.getResponseHolder().populateResponse(responseContext);

        return (NewCookie) headers.getFirst(HttpHeaders.SET_COOKIE);
    }
}
