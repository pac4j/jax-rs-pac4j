package org.pac4j.jax.rs.filters;

import static org.junit.Assert.*;

import org.junit.Test;

public class CallbackFilterTest {

    @Test
    public void renew_session_defaults_to_pac4j_default() {
        CallbackFilter filter = new CallbackFilter(null);

        assertNull(filter.getRenewSession());
        assertTrue(filter.isRenewSession());
    }

    @Test
    public void renew_session_can_be_disabled() {
        CallbackFilter filter = new CallbackFilter(null);
        filter.setRenewSession(false);

        assertEquals(Boolean.FALSE, filter.getRenewSession());
        assertFalse(filter.isRenewSession());
    }
}
