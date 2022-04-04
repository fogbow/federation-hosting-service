package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class FederationSpec {
    
    private String name;
    private Map<String, String> metadata;
    private String description;
    private boolean enabled;
    
    public FederationSpec() {
        
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
