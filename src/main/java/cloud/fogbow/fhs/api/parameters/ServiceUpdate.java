package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class ServiceUpdate {
    private Map<String, String> metadata;
    private String discoveryPolicy;
    private String accessPolicy;
    
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
