package cloud.fogbow.fhs.core.plugins.mapper;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class SystemToFederationMapperInstantiator {

    private FhsClassFactory classFactory;
    
    public SystemToFederationMapperInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public SystemToFederationMapper getPlugin(String pluginClassName, String federationName) {
        return (SystemToFederationMapper) classFactory.createPluginInstance(pluginClassName, federationName);
    }
}
