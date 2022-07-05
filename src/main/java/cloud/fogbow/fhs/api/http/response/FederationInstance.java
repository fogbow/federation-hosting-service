package cloud.fogbow.fhs.api.http.response;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(description, enabled, fedId, fedName, owningFedAdminId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FederationInstance other = (FederationInstance) obj;
        return Objects.equals(description, other.description) && enabled == other.enabled
                && Objects.equals(fedId, other.fedId) && Objects.equals(fedName, other.fedName)
                && Objects.equals(owningFedAdminId, other.owningFedAdminId);
    }
}
