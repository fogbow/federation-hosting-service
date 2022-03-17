package cloud.fogbow.fhs.api.parameters;

public class FederationSpec {
    
    private String name;
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
}
