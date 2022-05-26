package cloud.fogbow.fhs.api.http.response;

public class FederationInstance {
    private String fedId;
    private String fedName;
    private String description;
    private boolean enabled;
    private String owningFedAdminId;
    
    public FederationInstance() {
        
    }
    
    public FederationInstance(String fedId, String fedName, String description, boolean enabled,
            String owningFedAdminId) {
        super();
        this.fedId = fedId;
        this.fedName = fedName;
        this.description = description;
        this.enabled = enabled;
        this.owningFedAdminId = owningFedAdminId;
    }

    public String getFedId() {
        return fedId;
    }

    public String getFedName() {
        return fedName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getOwningFedAdminId() {
        return owningFedAdminId;
    }
}
