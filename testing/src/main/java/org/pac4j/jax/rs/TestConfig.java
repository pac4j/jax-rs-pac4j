package org.pac4j.jax.rs;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.jax.rs.pac4j.JaxRsAjaxRequestResolver;
import org.pac4j.jax.rs.pac4j.JaxRsUrlResolver;

public interface TestConfig {
    
    String DEFAULT_CLIENT = "default-form";
    
    default Config getConfig() {
        // login not used because the ajax resolver always answer true
        Authenticator<UsernamePasswordCredentials> auth = new SimpleTestUsernamePasswordAuthenticator();
        FormClient client = new FormClient("notUsedLoginUrl", auth);
        DirectFormClient client2 = new DirectFormClient(auth);
        DirectFormClient client3 = new DirectFormClient(auth);
        client3.setName(DEFAULT_CLIENT);

        Clients clients = new Clients("notUsedCallbackUrl", client, client2, client3);
        // in case of invalid credentials, we simply want the error, not a redirect to the login url
        clients.setAjaxRequestResolver(new JaxRsAjaxRequestResolver());
        
        // so that callback url have the correct prefix w.r.t. the container's context
        clients.setUrlResolver(new JaxRsUrlResolver());
        
        clients.setDefaultSecurityClients(DEFAULT_CLIENT);

        return new Config(clients);
    }
}
