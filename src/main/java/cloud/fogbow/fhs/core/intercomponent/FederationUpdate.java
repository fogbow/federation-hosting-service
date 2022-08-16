package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationUser;

public class FederationUpdate {
    private String targetFederationId;
    private String newName;
    private String newDescription;
    private Boolean newEnabled;
    private List<FederationUser> updatedMembers;
    private List<String> updatedServices;
    private List<FederationAttribute> updatedAttributes;
    private List<String> membersToDelete;
    private List<String> servicesToDelete;
    private List<String> attributesToDelete;
    private Map<String, String> updatedMetadata;
    private List<String> updatedFhss;
    private boolean completed;
    
    public FederationUpdate(String targetFederationId, String newName, String newDescription, Boolean newEnabled,
            List<FederationUser> updatedMembers, List<String> updatedServices,
            List<FederationAttribute> updatedAttributes, List<String> membersToDelete,
            List<String> servicesToDelete, List<String> attributesToDelete,
            Map<String, String> updatedMetadata) {
        this.targetFederationId = targetFederationId;
        this.newName = newName;
        this.newDescription = newDescription;
        this.newEnabled = newEnabled;
        this.updatedMembers = updatedMembers;
        this.updatedServices = updatedServices;
        this.updatedAttributes = updatedAttributes;
        this.membersToDelete = membersToDelete;
        this.servicesToDelete = servicesToDelete;
        this.attributesToDelete = attributesToDelete;
        this.updatedMetadata = updatedMetadata;
        this.updatedFhss = new ArrayList<String>();
        this.completed = false;
    }

    public String getTargetFederationId() {
        return targetFederationId;
    }
    
    public boolean updatedName() {
        return newName != null;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public boolean updatedDescription() {
        return newDescription != null;
    }
    
    public String getNewDescription() {
        return newDescription;
    }
    
    public boolean updatedEnabled() {
        return newEnabled != null;
    }
    
    public boolean getNewEnabled() {
        return newEnabled;
    }
    
    public List<FederationUser> getUpdatedMembers() {
        return updatedMembers;
    }
    
    public List<String> getUpdatedServices() {
        return updatedServices;
    }
    
    public List<FederationAttribute> getUpdatedAttributes() {
        return updatedAttributes;
    }

    public List<String> getMembersToDelete() {
        return membersToDelete;
    }

    public List<String> getServicesToDelete() {
        return servicesToDelete;
    }

    public List<String> getAttributesToDelete() {
        return attributesToDelete;
    }
    
    public Map<String, String> getUpdatedMetadata() {
        return updatedMetadata;
    }
    
    public void addUpdatedFhs(String updatedFhs) {
        this.updatedFhss.add(updatedFhs);
    }
    
    public List<String> getUpdatedFhss() {
        return this.updatedFhss;
    }
    
    public void setAsCompleted() {
        this.completed = true;
    }
    
    public boolean completed() {
        return this.completed;
    }
}
