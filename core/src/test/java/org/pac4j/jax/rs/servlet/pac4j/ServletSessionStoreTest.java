package org.pac4j.jax.rs.servlet.pac4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.session.SessionStore;

public class ServletSessionStoreTest {

    private HttpServletRequest request;

    private ServletJaxRsContext context;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        context = mock(ServletJaxRsContext.class);
        when(context.getRequest()).thenReturn(request);
    }

    @Test
    public void trackable_session_is_the_native_http_session() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        Optional<Object> trackableSession = ServletSessionStore.INSTANCE.getTrackableSession(context);

        assertTrue(trackableSession.isPresent());
        assertSame(session, trackableSession.get());
    }

    @Test
    public void no_trackable_session_without_native_session() {
        when(request.getSession(false)).thenReturn(null);

        assertFalse(ServletSessionStore.INSTANCE.getTrackableSession(context).isPresent());
    }

    @Test
    public void session_store_can_be_rebuilt_from_trackable_session() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("key")).thenReturn("value");
        when(request.getSession(false)).thenReturn(session);

        Object trackableSession = ServletSessionStore.INSTANCE.getTrackableSession(context).get();
        SessionStore rebuilt = ServletSessionStore.INSTANCE.buildFromTrackableSession(context, trackableSession).get();

        assertEquals(Optional.of("value"), rebuilt.get(context, "key"));
    }
}
