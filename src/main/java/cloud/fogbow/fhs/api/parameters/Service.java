package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class Service {
    private String ownerId;
    private String endpoint;
    private Map<String, String> metadata;
    private String discoveryPolicy;
    private String accessPolicy;
    
    public String getOwnerId() {
        return ownerId;
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
