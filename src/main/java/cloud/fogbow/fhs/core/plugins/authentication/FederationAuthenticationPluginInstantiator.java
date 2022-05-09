package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class FederationAuthenticationPluginInstantiator {

    private FhsClassFactory classFactory;
    
    public FederationAuthenticationPluginInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public FederationAuthenticationPlugin getAuthenticationPlugin(String authenticationPluginClassName, 
            Map<String, String> properties) {
        return (FederationAuthenticationPlugin) this.classFactory.createPluginInstance(authenticationPluginClassName, 
                new HashMap<String, String>(properties));
    }
}
