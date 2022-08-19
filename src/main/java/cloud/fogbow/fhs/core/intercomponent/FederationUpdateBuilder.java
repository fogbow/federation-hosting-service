package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FederationUpdateBuilder {
    private String targetFederationId;
    private String newName;
    private String newDescription;
    private Boolean newEnabled;
    private List<String> updatedMembers;
    private List<String> membersToDelete;
    private List<String> updatedServices;
    private List<String> servicesToDelete;
    private List<String> updatedAttributes;
    private List<String> attributesToDelete;
    private Map<String, String> updatedMetadata;
    
    public FederationUpdateBuilder() {
        targetFederationId = null;
        newName = null;
        newDescription = null;
        newEnabled = null;
        updatedMembers = new ArrayList<String>();
        updatedServices = new ArrayList<String>();
        updatedAttributes = new ArrayList<String>();
        membersToDelete = new ArrayList<String>();
        servicesToDelete = new ArrayList<String>();
        attributesToDelete = new ArrayList<String>();
        updatedMetadata = new HashMap<String, String>();
    }
    
    public FederationUpdateBuilder updateFederation(String federationId) {
        this.targetFederationId = federationId;
        return this;
    }
    
    public FederationUpdateBuilder withName(String newName) {
        this.newName = newName;
        return this;
    }
    
    public FederationUpdateBuilder withDescription(String newDescription) {
        this.newDescription = newDescription;
        return this;
    }
    
    public FederationUpdateBuilder withMember(String member) {
        this.updatedMembers.add(member);
        return this;
    }
    
    public FederationUpdateBuilder deleteMember(String memberId) {
        this.membersToDelete.add(memberId);
        return this;
    }
    
    public FederationUpdateBuilder withService(String service) {
        this.updatedServices.add(service);
        return this;
    }
    
    public FederationUpdateBuilder deleteService(String serviceId) {
        this.servicesToDelete.add(serviceId);
        return this;
    }
    
    public FederationUpdateBuilder withAttribute(String attribute) {
        this.updatedAttributes.add(attribute);
        return this;
    }
    
    public FederationUpdateBuilder deleteAttribute(String attributeId) {
        this.attributesToDelete.add(attributeId);
        return this;
    }
    
    public FederationUpdateBuilder withMetadata(Map<String, String> updatedMetadata) {
        this.updatedMetadata = updatedMetadata;
        return this;
    }
    
    public FederationUpdate build() {
        return new FederationUpdate(true, targetFederationId, newName, newDescription, 
                newEnabled, updatedMembers, updatedServices, updatedAttributes, membersToDelete, 
                servicesToDelete, attributesToDelete, updatedMetadata);
    }
}
