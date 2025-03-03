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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsContext implements WebContext {

    public static final String RESPONSE_HOLDER = JaxRsContext.class + ".ResponseHolder";

    private final RequestJaxRsContext requestJaxRsContext;
    private final ContainerRequestContext containerRequestContext;

    private ResponseBuilder abortResponse = null;

    private MultivaluedMap<String, String> parameters = null;

    public JaxRsContext(RequestJaxRsContext requestJaxRsContext) {
        this.requestJaxRsContext = requestJaxRsContext;
        this.containerRequestContext = requestJaxRsContext.getRequestContext();
    }

    public Providers getProviders() {
        return this.requestJaxRsContext.getProviders();
    }

    public ContainerRequestContext getRequestContext() {
        return this.requestJaxRsContext.getRequestContext();
    }

    public ResponseBuilder getAbortBuilder() {
        if (abortResponse == null) {
            abortResponse = Response.ok();
        }
        return abortResponse;
    }

    public ResponseHolder getResponseHolder() {
        ContainerRequestContext requestContext = this.requestJaxRsContext.getRequestContext();
        ResponseHolder prop = (ResponseHolder) requestContext.getProperty(RESPONSE_HOLDER);
        if (prop == null) {
            prop = new ResponseHolder();
            requestContext.setProperty(RESPONSE_HOLDER, prop);
        }
        return prop;
    }

    @Override
    public Optional<String> getResponseHeader(String name) {
        return Optional.ofNullable(getResponseHolder().getResponseHeader(name));
    }

    public static class ResponseHolder {

        private boolean hasResponseContent = false;

        private String responseContent = null;

        private boolean hasResponseStatus = false;

        private int responseStatus = 0;

        private boolean hasResponseContentType = false;

        private MediaType responseContentType = null;

        private final Map<String, String> responseHeaders = new HashMap<>();

        private final Set<NewCookie> responseCookies = new HashSet<>();

        public void writeResponseContent(String content) {
            responseContent = content;
            hasResponseContent = true;
        }

        public void setResponseStatus(int code) {
            responseStatus = code;
            hasResponseStatus = true;
        }

        public void setResponseHeader(String name, String value) {
            responseHeaders.put(name, value);
        }

        public void addResponseCookie(NewCookie cookie) {
            responseCookies.add(cookie);
        }

        public void setResponseContentType(MediaType type) {
            responseContentType = type;
            hasResponseContentType = true;
        }

        public String getResponseHeader(String name) {
            return responseHeaders.get(name);
        }

        public void populateResponse(ContainerResponseContext responseContext) {
            if (hasResponseContent) {
                responseContext.setEntity(responseContent);
            }
            if (hasResponseContentType) {
                responseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, responseContentType);
            }
            if (hasResponseStatus) {
                responseContext.setStatus(responseStatus);
            }
            for (Entry<String, String> headers : responseHeaders.entrySet()) {
                responseContext.getHeaders().putSingle(headers.getKey(), headers.getValue());
            }
            for (NewCookie cookie : responseCookies) {
                responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
            }
        }
    }

    @Override
    public void setResponseHeader(String name, String value) {
        CommonHelper.assertNotNull("name", name);
        // header() adds headers, so we must remove the previous value first
        getAbortBuilder().header(name, null);
        getAbortBuilder().header(name, value);
        getResponseHolder().setResponseHeader(name, value);
    }

    @Override
    public void setResponseContentType(String content) {
        MediaType type = content == null ? null : MediaType.valueOf(content);
        getAbortBuilder().type(type);
        getResponseHolder().setResponseContentType(type);
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        CommonHelper.assertNotNull("cookie", cookie);
        NewCookie c = new NewCookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), "",
                cookie.getMaxAge(), cookie.isSecure());
        getAbortBuilder().cookie(c);
        getResponseHolder().addResponseCookie(c);
    }

    /**
     * This gives us what is after the baseURI (e.g., the servlet context + the
     * servlet mapping)
     */
    @Override
    public String getPath() {
        // pac4j expects a URL starting with /
        return "/" + this.containerRequestContext.getUriInfo().getPath();
    }

    public String getAbsolutePath(String relativePath, boolean full) {
        if (relativePath == null) {
            return null;
        } else if (relativePath.startsWith("/")) {
            URI baseUri = this.containerRequestContext.getUriInfo().getBaseUri();
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
    public Optional<String> getRequestParameter(String name) {
        return Optional.ofNullable(extractedParameters().getFirst(name));
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return transform(extractedParameters(), l -> l.toArray(new String[l.size()]));
    }

    private static <X, Y, Z> Map<X, Z> transform(Map<? extends X, ? extends Y> input, Function<Y, Z> function) {
        return input.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> function.apply(e.getValue())));
    }

    private MultivaluedMap<String, String> extractedParameters() {
        ContainerRequestContext requestContext = this.containerRequestContext;
        Providers providers = this.requestJaxRsContext.getProviders();
        if (parameters == null) {
            MultivaluedHashMap<String, String> multivaluedHashMap = new MultivaluedHashMap<>();
            // efficient
            multivaluedHashMap.putAll(requestContext.getUriInfo().getQueryParameters());
            parameters = multivaluedHashMap;
            if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(requestContext.getMediaType())) {
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
    public Optional<Object> getRequestAttribute(String name) {
        return Optional.ofNullable(this.containerRequestContext.getProperty(name));
    }

    @Override
    public void setRequestAttribute(String name, Object value) {
        this.containerRequestContext.setProperty(name, value);
    }

    @Override
    public Optional<String> getRequestHeader(String name) {
        return Optional.ofNullable(this.containerRequestContext.getHeaderString(name));
    }

    @Override
    public String getRequestMethod() {
        return this.containerRequestContext.getMethod();
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
        return this.containerRequestContext.getUriInfo().getRequestUri();
    }

    @Override
    public boolean isSecure() {
        // in jax-rs the security context is never null
        return this.containerRequestContext.getSecurityContext().isSecure();
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        return this.containerRequestContext.getCookies().values().stream().map(c -> {
            Cookie nc = new Cookie(c.getName(), c.getValue());
            nc.setDomain(c.getDomain());
            nc.setPath(c.getPath());
            return nc;
        }).collect(Collectors.toList());
    }

    @Override
    public String getRequestContent() {
        return readAndResetEntityStream(stream -> {
            String charsetS = this.containerRequestContext.getMediaType().getParameters().get(MediaType.CHARSET_PARAMETER);
            Charset charset;
            if (charsetS != null) {
                charset = Charset.forName(charsetS);
            } else {
                charset = Charset.defaultCharset();
            }

            // TODO newlines?! this is copied from J2EContext
            String content = new BufferedReader(new InputStreamReader(stream, charset)).lines().reduce("",
                    (accumulator, actual) -> accumulator.concat(actual));
            return content;
        });
    }

    private <T> T readAndResetEntityStream(Function<InputStream, T> f) {
        try (InputStream entityStream = this.containerRequestContext.getEntityStream()) {
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
                this.containerRequestContext.setEntityStream(stream);
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }
}
