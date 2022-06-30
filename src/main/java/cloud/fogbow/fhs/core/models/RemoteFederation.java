package cloud.fogbow.fhs.core.models;

import java.util.Objects;

public class RemoteFederation {
    private String fedId;
    private String fedName;
    private String description;
    private boolean enabled;
    private String owningFedAdminId;
    private String ownerFhsId;
    
    public RemoteFederation() {
        
    }
    
    public RemoteFederation(String fedId, String fedName, String description, boolean enabled,
            String owningFedAdminId, String ownerFhsId) {
        super();
        this.fedId = fedId;
        this.fedName = fedName;
        this.description = description;
        this.enabled = enabled;
        this.owningFedAdminId = owningFedAdminId;
        this.ownerFhsId = ownerFhsId;
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

    public String getOwnerFhsId() {
        return ownerFhsId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fedId, ownerFhsId, owningFedAdminId);
    }

    // TODO test
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteFederation other = (RemoteFederation) obj;
        return Objects.equals(fedId, other.fedId) && Objects.equals(ownerFhsId, other.ownerFhsId)
                && Objects.equals(owningFedAdminId, other.owningFedAdminId);
    }
}
