package cloud.fogbow.fhs.core.plugins.discovery;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class DiscoveryPolicyInstantiator {
    private FhsClassFactory classFactory;
    
    public DiscoveryPolicyInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public ServiceDiscoveryPolicy getDiscoveryPolicy(String discoveryPolicyClassName, String ... args) {
        return (ServiceDiscoveryPolicy) this.classFactory.createPluginInstance(discoveryPolicyClassName, args);
    }
}
