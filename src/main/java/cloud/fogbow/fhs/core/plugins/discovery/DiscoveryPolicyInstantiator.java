package cloud.fogbow.fhs.core.plugins.discovery;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class DiscoveryPolicyInstantiator {
    private FhsClassFactory classFactory;
    
    public DiscoveryPolicyInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public DiscoveryPolicyInstantiator(FhsClassFactory classFactory) {
        this.classFactory = classFactory;
    }
    
    public ServiceDiscoveryPolicy getDiscoveryPolicy(String discoveryPolicyClassName, String ... args) {
        if (discoveryPolicyClassName == null || discoveryPolicyClassName.isEmpty()) {
            return new AllowAllServiceDiscoveryPolicy();
        } else {
            return (ServiceDiscoveryPolicy) this.classFactory.createPluginInstance(discoveryPolicyClassName, args);
        }
    }
}
