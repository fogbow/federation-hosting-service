package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

public class ServiceDiscovered {
    private String serviceId;
    private Map<String, String> metadata;
    private String endpoint;
    
    public ServiceDiscovered(String serviceId, Map<String, String> metadata, String endpoint) {
        this.serviceId = serviceId;
        this.metadata = metadata;
        this.endpoint = endpoint;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
}
