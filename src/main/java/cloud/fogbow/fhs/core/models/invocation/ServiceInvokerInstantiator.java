package cloud.fogbow.fhs.core.models.invocation;

import java.util.Map;

import cloud.fogbow.fhs.core.FhsClassFactory;
import cloud.fogbow.fhs.core.models.ServiceInvoker;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class ServiceInvokerInstantiator {
    private FhsClassFactory classFactory;
    
    public ServiceInvokerInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public ServiceInvoker getInvoker(String className, Map<String, String> metadata, String federationId) {
        // FIXME constant
        metadata.put("federationId", federationId);
        return (ServiceInvoker) this.classFactory.createPluginInstance(className, 
                new MapUtils().serializeMap(metadata));
    }
}
