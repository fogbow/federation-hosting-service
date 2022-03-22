package cloud.fogbow.fhs.core.models.invocation;

import cloud.fogbow.fhs.core.FhsClassFactory;
import cloud.fogbow.fhs.core.models.ServiceInvoker;

public class ServiceInvokerInstantiator {
    private FhsClassFactory classFactory;
    
    public ServiceInvokerInstantiator() {
        this.classFactory = new FhsClassFactory();
    }
    
    public ServiceInvoker getInvoker(String className, String ... args) {
        return (ServiceInvoker) this.classFactory.createPluginInstance(className, args);
    }
}
