package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.engine.DefaultSecurityLogic;

/**
 * @author Victor Noel - Linagora
 * @since 1.1.1
 * 
 */
public class JaxRsSecurityLogic<C extends JaxRsContext> extends DefaultSecurityLogic<Object, C> {

    public JaxRsSecurityLogic() {
        setProfileManagerFactory(JaxRsProfileManager::new);
    }

}
