package org.pac4j.jax.rs.pac4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsContext implements WebContext {

    private final ContainerRequestContext requestContext;

    private final JaxRsSessionStore sessionStore;

    private final Providers providers;

    private ResponseBuilder abortResponse = null;

    private MultivaluedMap<String, String> parameters = null;

    public JaxRsContext(Providers providers, ContainerRequestContext requestContext, JaxRsSessionStore sessionStore) {
        this.providers = providers;
        this.requestContext = requestContext;
        this.sessionStore = sessionStore;
    }

    public Providers getProviders() {
        return providers;
    }

    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }

    public JaxRsSessionStore getSessionStore() {
        return sessionStore;
    }

    public ResponseBuilder getAbortBuilder() {
        if (abortResponse == null) {
            abortResponse = Response.ok();
        }
        return abortResponse;
    }

    @Override
    public void writeResponseContent(String content) {
        getAbortBuilder().entity(content);
    }

    @Override
    public void setResponseStatus(int code) {
        getAbortBuilder().status(code);
    }

    @Override
    public void setResponseHeader(String name, String value) {
        // header() adds headers, so we must remove the previous value first
        getAbortBuilder().header(name, null);
        getAbortBuilder().header(name, value);
    }

    @Override
    public void setResponseContentType(String content) {
        getAbortBuilder().type(content);
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        // Note: expiry is not in servlet and is meant to be superseeded by
        // max-age, so we simply make it null
        getAbortBuilder().cookie(new NewCookie(cookie.getName(), cookie.getValue(), cookie.getPath(),
                cookie.getDomain(), cookie.getVersion(), cookie.getComment(), cookie.getMaxAge(), null,
                cookie.isSecure(), cookie.isHttpOnly()));
    }

    /**
     * This gives us what is after the baseURI (e.g., the servlet context + the
     * servlet mapping)
     */
    @Override
    public String getPath() {
        // pac4j expects a URL starting with /
        return "/" + requestContext.getUriInfo().getPath();
    }

    public String getAbsolutePath(String relativePath, boolean full) {
        if (relativePath == null) {
            return null;
        } else if (relativePath.startsWith("/")) {
            URI baseUri = requestContext.getUriInfo().getBaseUri();
            String urlPrefix;
            if (full) {
                urlPrefix = baseUri.toString();
            } else {
                urlPrefix = baseUri.getPath();
            }
            // urlPrefix already contains the ending /
            return urlPrefix + relativePath.substring(1);
        } else {
            return relativePath;
        }
    }

    @Override
    public String getRequestParameter(String name) {
        return extractedParameters().getFirst(name);
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return transform(extractedParameters(), l -> l.toArray(new String[l.size()]));
    }

    private static <X, Y, Z> Map<X, Z> transform(Map<? extends X, ? extends Y> input, Function<Y, Z> function) {
        return input.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> function.apply(e.getValue())));
    }

    private MultivaluedMap<String, String> extractedParameters() {
        if (parameters == null) {
            MultivaluedHashMap<String, String> multivaluedHashMap = new MultivaluedHashMap<>();
            // efficient
            multivaluedHashMap.putAll(requestContext.getUriInfo().getQueryParameters());
            parameters = multivaluedHashMap;
            if (MediaType.APPLICATION_FORM_URLENCODED
                    .equalsIgnoreCase(Objects.toString(requestContext.getMediaType()))) {
                readAndResetEntityStream(stream -> {
                    try {
                        Form form = providers.getMessageBodyReader(Form.class, Form.class, new Annotation[0],
                                MediaType.APPLICATION_FORM_URLENCODED_TYPE).readFrom(Form.class, Form.class,
                                        new Annotation[0], MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                                        requestContext.getHeaders(), stream);
                        form.asMap().forEach(parameters::addAll);
                        return null;
                    } catch (IOException e) {
                        throw new TechnicalException(e);
                    }
                });
            }
        }
        return parameters;
    }

    @Override
    public Object getRequestAttribute(String name) {
        return requestContext.getProperty(name);
    }

    @Override
    public void setRequestAttribute(String name, Object value) {
        requestContext.setProperty(name, value);
    }

    @Override
    public String getRequestHeader(String name) {
        return requestContext.getHeaderString(name);
    }

    @Override
    public void setSessionAttribute(String name, Object value) {
        sessionStore.set(this, name, value);
    }

    @Override
    public Object getSessionAttribute(String name) {
        return sessionStore.get(this, name);
    }

    @Override
    public Object getSessionIdentifier() {
        return sessionStore.getOrCreateSessionId(this);
    }

    @Override
    public void invalidationSession() {
        sessionStore.invalidateSession(this);
    }

    public void renewSession() {
        sessionStore.renewSession(this);
    }

    @Override
    public String getRequestMethod() {
        return requestContext.getMethod();
    }

    @Override
    public String getRemoteAddr() {
        // TODO Unavailable in JAX-RS 2.0.1. See https://java.net/jira/browse/JERSEY-473
        return null;
    }

    @Override
    public String getServerName() {
        return getRequestUri().getHost();
    }

    @Override
    public int getServerPort() {
        return getRequestUri().getPort();
    }

    @Override
    public String getScheme() {
        return getRequestUri().getScheme();
    }

    @Override
    public String getFullRequestURL() {
        return getRequestUri().toString();
    }

    private URI getRequestUri() {
        return requestContext.getUriInfo().getRequestUri();
    }

    @Override
    public boolean isSecure() {
        // in jax-rs the security context is never null
        return requestContext.getSecurityContext().isSecure();
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        return requestContext.getCookies().values().stream().map(c -> {
            Cookie nc = new Cookie(c.getName(), c.getValue());
            nc.setDomain(c.getDomain());
            nc.setPath(c.getPath());
            nc.setVersion(c.getVersion());
            return nc;
        }).collect(Collectors.toList());
    }

    @Override
    public String getRequestContent() {
        return readAndResetEntityStream(stream -> {
            String charsetS = requestContext.getMediaType().getParameters().get(MediaType.CHARSET_PARAMETER);
            Charset charset;
            if (charsetS != null) {
                charset = Charset.forName(charsetS);
            } else {
                charset = Charset.defaultCharset();
            }

            // TODO newlines?! this is copied from J2EContext
            @SuppressWarnings("OS_OPEN_STREAM")
            String content = new BufferedReader(new InputStreamReader(stream, charset)).lines().reduce("",
                    (accumulator, actual) -> accumulator.concat(actual));
            return content;
        });
    }

    private <T> T readAndResetEntityStream(Function<InputStream, T> f) {
        try (InputStream entityStream = requestContext.getEntityStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = entityStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            ByteArrayInputStream stream = new ByteArrayInputStream(baos.toByteArray());
            try {
                return f.apply(stream);
            } finally {
                stream.reset();
                requestContext.setEntityStream(stream);
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }
}
