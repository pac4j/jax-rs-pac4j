package org.pac4j.jax.rs.filters;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.engine.LogoutLogic;

public class LogoutFilterTest {

    private ContainerRequestContext requestContext;

    private LogoutLogic logoutLogic;

    private LogoutFilter filter;

    @Before
    public void setUp() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8080/app/api/"));
        requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);

        logoutLogic = mock(LogoutLogic.class);
        filter = new LogoutFilter(null);
        filter.setLogoutLogic(logoutLogic);
    }

    @Test
    public void relative_default_url_is_prefixed_by_the_base_path() throws IOException {
        filter.setDefaultUrl("/home");
        filter.setLogoutUrlPattern("/.*");

        filter.filter(new Config(), requestContext);

        verify(logoutLogic).perform(any(Config.class), eq("/app/api/home"), eq("/app/api/.*"), isNull(), isNull(),
                isNull(), any(FrameworkParameters.class));
    }

    @Test
    public void absolute_default_url_is_left_unchanged() throws IOException {
        filter.setDefaultUrl("https://example.com/bye");

        filter.filter(new Config(), requestContext);

        verify(logoutLogic).perform(any(Config.class), eq("https://example.com/bye"), isNull(), isNull(), isNull(),
                isNull(), any(FrameworkParameters.class));
    }
}
