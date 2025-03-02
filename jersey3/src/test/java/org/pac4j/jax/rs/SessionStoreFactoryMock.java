package org.pac4j.jax.rs;

import org.mockito.Mockito;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

public class SessionStoreFactoryMock implements SessionStoreFactory {

    public static final SessionStoreFactory INSTANCE = new SessionStoreFactoryMock();

    private static final SessionStore MOCK_SESSION_STORE = Mockito.mock(SessionStore.class);

    @Override
    public SessionStore newSessionStore(FrameworkParameters parameters) {
        return MOCK_SESSION_STORE;
    }
}
