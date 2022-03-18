package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

public class ServiceInfo {
    private String serviceId;
    private String endpoint;
    private Map<String, String> metadata;
    private String discoveryPolicy;
    private String accessPolicy;
    
    public ServiceInfo(String serviceId, String endpoint, Map<String, String> metadata, String discoveryPolicy,
            String accessPolicy) {
        this.serviceId = serviceId;
        this.endpoint = endpoint;
        this.metadata = metadata;
        this.discoveryPolicy = discoveryPolicy;
        this.accessPolicy = accessPolicy;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getDiscoveryPolicy() {
        return discoveryPolicy;
    }

    public String getAccessPolicy() {
        return accessPolicy;
    }
}
