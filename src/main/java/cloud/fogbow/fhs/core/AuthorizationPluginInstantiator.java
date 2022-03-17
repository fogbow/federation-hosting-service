package cloud.fogbow.fhs.core;

import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.plugins.authorization.ComposedAuthorizationPlugin;
import cloud.fogbow.fhs.core.models.FhsOperation;

public class AuthorizationPluginInstantiator {
    private static FhsClassFactory classFactory = new FhsClassFactory();
    
    public static AuthorizationPlugin<FhsOperation> getAuthorizationPlugin(String className) {
        AuthorizationPlugin<FhsOperation> plugin = 
                (AuthorizationPlugin<FhsOperation>) AuthorizationPluginInstantiator.classFactory.createPluginInstance(className);
        if (className.equals("cloud.fogbow.common.plugins.authorization.ComposedAuthorizationPlugin")) {
            ComposedAuthorizationPlugin<FhsOperation> composedPlugin = (ComposedAuthorizationPlugin<FhsOperation>) plugin;
            composedPlugin.startPlugin(classFactory);
        }
        return plugin;
    }
}
