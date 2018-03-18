package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.config.Config;

public class JaxRsConfig extends Config {

    protected String defaultClients;
    
    public void setDefaultClients(String defaultClients) {
        this.defaultClients = defaultClients;
    }
    
    public String getDefaultClients() {
        return defaultClients;
    }
}
