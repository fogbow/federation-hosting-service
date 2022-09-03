package cloud.fogbow.fhs.core.plugins.invocation;

import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.fhs.core.FhsClassFactory;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class ServiceInvokerInstantiator {
    @VisibleForTesting
    static final String FEDERATION_ID_KEY = "federationId";

    private FhsClassFactory classFactory;
    private MapUtils mapUtils;
    
    public ServiceInvokerInstantiator(FhsClassFactory classFactory, MapUtils mapUtils) {
        this.classFactory = classFactory;
        this.mapUtils = mapUtils;
    }

    public ServiceInvokerInstantiator() {
        this.classFactory = new FhsClassFactory();
        this.mapUtils = new MapUtils();
    }
    
    public ServiceInvoker getInvoker(String className, Map<String, String> metadata, String federationId) {
        if (className == null || className.isEmpty()) {
            return new DefaultServiceInvoker();
        } else {
            metadata.put(FEDERATION_ID_KEY, federationId);
            return (ServiceInvoker) this.classFactory.createPluginInstance(className, 
                    this.mapUtils.serializeMap(metadata));
        }
    }
}
