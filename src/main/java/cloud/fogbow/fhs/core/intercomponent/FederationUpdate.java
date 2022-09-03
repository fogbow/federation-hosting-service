package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "federation_update_table")
public class FederationUpdate {
    private static final String FEDERATION_UPDATE_ID_COLUMN_NAME = "federation_update_id";
    private static final String FEDERATION_UPDATE_ISLOCAL_COLUMN_NAME = "federation_update_islocal";
    private static final String FEDERATION_UPDATE_TARGET_FEDERATION_ID_COLUMN_NAME = "federation_update_target_federation_id";
    private static final String FEDERATION_UPDATE_NEW_NAME_COLUMN_NAME = "federation_update_new_name";
    private static final String FEDERATION_UPDATE_NEW_DESCRIPTION_COLUMN_NAME = "federation_update_new_description";
    private static final String FEDERATION_UPDATE_NEW_ENABLED_COLUMN_NAME = "federation_update_new_enabled";
    private static final String FEDERATION_UPDATE_UPDATED_MEMBERS_COLUMN_NAME = "federation_update_updated_members";
    private static final String FEDERATION_UPDATE_UPDATED_SERVICES_COLUMN_NAME = "federation_update_updated_services";
    private static final String FEDERATION_UPDATE_UPDATED_ATTRIBUTES_COLUMN_NAME = "federation_update_updated_attributes";
    private static final String FEDERATION_UPDATE_MEMBERS_TO_DELETE_COLUMN_NAME = "federation_update_members_to_delete";
    private static final String FEDERATION_UPDATE_SERVICES_TO_DELETE_COLUMN_NAME = "federation_update_services_to_delete";
    private static final String FEDERATION_UPDATE_ATTRIBUTES_TO_DELETE_COLUMN_NAME = "federation_update_attributes_to_delete";
    private static final String FEDERATION_UPDATE_UPDATED_METADATA_COLUMN_NAME = "federation_update_updated_metadata";
    private static final String FEDERATION_UPDATE_UPDATED_FHSS_COLUMN_NAME = "federation_update_updated_fhss";
    private static final String FEDERATION_UPDATE_COMPLETED_COLUMN_NAME = "federation_update_completed";

    @Column(name = FEDERATION_UPDATE_ID_COLUMN_NAME)
    @Id
    private String id;
    
    @Column(name = FEDERATION_UPDATE_ISLOCAL_COLUMN_NAME)
    private boolean local;
    
    @Column(name = FEDERATION_UPDATE_TARGET_FEDERATION_ID_COLUMN_NAME)
    private String targetFederationId;
    
    @Column(name = FEDERATION_UPDATE_NEW_NAME_COLUMN_NAME)
    private String newName;
    
    @Column(name = FEDERATION_UPDATE_NEW_DESCRIPTION_COLUMN_NAME)
    private String newDescription;
    
    @Column(name = FEDERATION_UPDATE_NEW_ENABLED_COLUMN_NAME)
    private Boolean newEnabled;
    
    @Column(name = FEDERATION_UPDATE_UPDATED_MEMBERS_COLUMN_NAME, columnDefinition="text", length=10485760)
    @ElementCollection
    private List<String> updatedMembers;
    
    @Column(name = FEDERATION_UPDATE_UPDATED_SERVICES_COLUMN_NAME, columnDefinition="text", length=10485760)
    @ElementCollection
    private List<String> updatedServices;
    
    @Column(name = FEDERATION_UPDATE_UPDATED_ATTRIBUTES_COLUMN_NAME)
    @ElementCollection
    private List<String> updatedAttributes;
    
    @Column(name = FEDERATION_UPDATE_MEMBERS_TO_DELETE_COLUMN_NAME)
    @ElementCollection
    private List<String> membersToDelete;
    
    @Column(name = FEDERATION_UPDATE_SERVICES_TO_DELETE_COLUMN_NAME)
    @ElementCollection
    private List<String> servicesToDelete;
    
    @Column(name = FEDERATION_UPDATE_ATTRIBUTES_TO_DELETE_COLUMN_NAME)
    @ElementCollection
    private List<String> attributesToDelete;
    
    @Column(name = FEDERATION_UPDATE_UPDATED_METADATA_COLUMN_NAME)
    @ElementCollection
    private Map<String, String> updatedMetadata;
    
    @Column(name = FEDERATION_UPDATE_UPDATED_FHSS_COLUMN_NAME)
    @ElementCollection
    private List<String> updatedFhss;
    
    @Column(name = FEDERATION_UPDATE_COMPLETED_COLUMN_NAME)
    private boolean completed;
    
    public FederationUpdate() {
        
    }
    
    public FederationUpdate(boolean local, String targetFederationId, String newName, 
            String newDescription, Boolean newEnabled, List<String> updatedMembers, 
            List<String> updatedServices, List<String> updatedAttributes, 
            List<String> membersToDelete, List<String> servicesToDelete, 
            List<String> attributesToDelete, Map<String, String> updatedMetadata) {
        this.id = UUID.randomUUID().toString();
        this.local = local;
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

    public String getId() {
        return id;
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
    
    public List<String> getUpdatedMembers() {
        return updatedMembers;
    }
    
    public List<String> getUpdatedServices() {
        return updatedServices;
    }
    
    public List<String> getUpdatedAttributes() {
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

    public boolean isLocal() {
        return local;
    }

    public void local() {
        this.local = true;
    }

    public void remote() {
        this.local = false;
    }
}
