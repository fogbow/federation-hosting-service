package cloud.fogbow.fhs.core.plugins.access;

import java.util.Map;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class AccessPolicyInstantiator {
    // FIXME this constant should be placed somewhere else
    private static final String ACCESS_POLICY_RULES_KEY = "accessPolicyRules";
    
    private FhsClassFactory classFactory;
    
    public AccessPolicyInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public ServiceAccessPolicy getAccessPolicy(String accessPolicyClassName, Map<String, String> metadata) {
        return (ServiceAccessPolicy) this.classFactory.createPluginInstance(
                accessPolicyClassName, metadata.get(ACCESS_POLICY_RULES_KEY));
    }
}
