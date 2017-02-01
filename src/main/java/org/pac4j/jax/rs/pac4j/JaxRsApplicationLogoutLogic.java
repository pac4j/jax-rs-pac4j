package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.engine.DefaultApplicationLogoutLogic;

/**
 * @author Victor Noel - Linagora
 * @since 1.1.1
 * 
 */
public class JaxRsApplicationLogoutLogic<C extends JaxRsContext> extends DefaultApplicationLogoutLogic<Object, C> {

    public JaxRsApplicationLogoutLogic() {
        setProfileManagerFactory(JaxRsProfileManager::new);
    }

}
