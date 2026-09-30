package org.pac4j.jax.rs.pac4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pac4j.core.context.Cookie;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;

public class JaxRsContextTest {

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

    @Test
    public void response_is_populated_only_once() {
        context.addResponseCookie(new Cookie("name", "value"));

        MultivaluedMap<String, Object> headers = populateResponse();
        populateResponse(headers);

        assertEquals(1, headers.get(HttpHeaders.SET_COOKIE).size());
    }

    @Test
    public void server_port_is_the_explicit_port() {
        mockRequestUri("http://example.com:8080/app");

        assertEquals(8080, context.getServerPort());
    }

    @Test
    public void server_port_defaults_to_80_for_http() {
        mockRequestUri("http://example.com/app");

        assertEquals(80, context.getServerPort());
    }

    @Test
    public void server_port_defaults_to_443_for_https() {
        mockRequestUri("https://example.com/app");

        assertEquals(443, context.getServerPort());
    }

    @Test
    public void request_content_without_media_type_keeps_newlines() {
        when(requestContext.getMediaType()).thenReturn(null);
        when(requestContext.getEntityStream())
                .thenReturn(new ByteArrayInputStream("line1\nline2 é".getBytes(StandardCharsets.UTF_8)));

        assertEquals("line1\nline2 é", context.getRequestContent());
    }

    @Test
    public void request_content_can_still_be_read_afterwards() throws Exception {
        when(requestContext.getEntityStream())
                .thenReturn(new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8)));

        context.getRequestContent();

        ArgumentCaptor<InputStream> stream = ArgumentCaptor.forClass(InputStream.class);
        verify(requestContext).setEntityStream(stream.capture());
        assertEquals("content", new String(stream.getValue().readAllBytes(), StandardCharsets.UTF_8));
    }

    private void mockRequestUri(String uri) {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(URI.create(uri));
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    private NewCookie addAndGetResponseCookie(Cookie cookie) {
        context.addResponseCookie(cookie);
        return (NewCookie) populateResponse().getFirst(HttpHeaders.SET_COOKIE);
    }

    private MultivaluedMap<String, Object> populateResponse() {
        return populateResponse(new MultivaluedHashMap<>());
    }

    private MultivaluedMap<String, Object> populateResponse(MultivaluedMap<String, Object> headers) {
        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        when(responseContext.getHeaders()).thenReturn(headers);
        context.getResponseHolder().populateResponse(responseContext);
        return headers;
    }
}
