package org.pac4j.jax.rs.grizzly.pac4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.session.SessionStore;

public class GrizzlySessionStoreTest {

    private Request request;

    private GrizzlyJaxRsContext context;

    @Before
    public void setUp() {
        request = mock(Request.class);
        context = mock(GrizzlyJaxRsContext.class);
        when(context.getRequest()).thenReturn(request);
    }

    @Test
    public void reading_does_not_create_a_session() {
        assertFalse(GrizzlySessionStore.INSTANCE.get(context, "key").isPresent());
        assertFalse(GrizzlySessionStore.INSTANCE.getSessionId(context, false).isPresent());
        assertFalse(GrizzlySessionStore.INSTANCE.getTrackableSession(context).isPresent());
        assertFalse(GrizzlySessionStore.INSTANCE.destroySession(context));
        assertFalse(GrizzlySessionStore.INSTANCE.renewSession(context));

        verifyNoSessionCreated();
    }

    @Test
    public void removing_a_value_does_not_create_a_session() {
        GrizzlySessionStore.INSTANCE.set(context, "key", null);

        verifyNoSessionCreated();
    }

    @Test
    public void setting_a_value_creates_a_session() {
        Session session = mock(Session.class);
        when(request.getSession(true)).thenReturn(session);

        GrizzlySessionStore.INSTANCE.set(context, "key", "value");

        verify(session).setAttribute("key", "value");
    }

    @Test
    public void session_id_is_created_on_demand() {
        Session session = mock(Session.class);
        when(session.getIdInternal()).thenReturn("id");
        when(request.getSession(true)).thenReturn(session);

        assertEquals(Optional.of("id"), GrizzlySessionStore.INSTANCE.getSessionId(context, true));
    }

    @Test
    public void existing_session_is_read() {
        Session session = mock(Session.class);
        when(session.getAttribute("key")).thenReturn("value");
        when(request.getSession(false)).thenReturn(session);

        assertEquals(Optional.of("value"), GrizzlySessionStore.INSTANCE.get(context, "key"));
    }

    @Test
    public void session_store_can_be_rebuilt_from_trackable_session() {
        Session session = mock(Session.class);
        when(session.getAttribute("key")).thenReturn("value");
        when(request.getSession(false)).thenReturn(session);

        Optional<Object> trackableSession = GrizzlySessionStore.INSTANCE.getTrackableSession(context);
        assertTrue(trackableSession.isPresent());
        assertSame(session, trackableSession.get());

        SessionStore rebuilt = GrizzlySessionStore.INSTANCE.buildFromTrackableSession(context, trackableSession.get())
                .get();
        assertEquals(Optional.of("value"), rebuilt.get(context, "key"));
    }

    private void verifyNoSessionCreated() {
        verify(request, never()).getSession();
        verify(request, never()).getSession(true);
    }
}
