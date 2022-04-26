package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;

public class Federation {
    private static final String SERVICE_OWNER_ATTRIBUTE_NAME = "serviceOwner";
    
    private String id;
    private String owner;
    private String name;
    private String description;
    private boolean enabled;
    private List<FederationUser> members;
    private List<FederationService> services;
    private List<FederationAttribute> attributes;
    private Map<String, String> metadata;
    private FederationAttribute serviceOwnerAttribute;
    
    public Federation(String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(UUID.randomUUID().toString(), owner, name, metadata, description, enabled);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(id, owner, name, metadata, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<FederationService>(), new ArrayList<FederationAttribute>());
        
        // FIXME
        this.serviceOwnerAttribute = new FederationAttribute(SERVICE_OWNER_ATTRIBUTE_NAME,
                SERVICE_OWNER_ATTRIBUTE_NAME);
        this.attributes.add(serviceOwnerAttribute);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled, 
            List<FederationUser> members, List<FederationService> services, 
            List<FederationAttribute> attributes) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.metadata = metadata;
        this.description = description;
        this.enabled = enabled;
        this.members = members;
        this.services = services;
        this.attributes = attributes;
    }

    // FIXME should receive email and description
    public FederationUser addUser(String userId) {
        FederationUser newMember = new FederationUser(userId, "", "", true);
        this.members.add(newMember);
        return newMember;
    }

    public FederationUser getUserById(String userId) throws InvalidParameterException {
        for (FederationUser member : members) {
            if (member.getName().equals(userId)) {
                return member;
            }
        }
        
        throw new InvalidParameterException(
                String.format(Messages.Exception.MEMBER_NOT_FOUND_IN_FEDERATION, userId, this.id));
    }
    
    public FederationUser getUserByMemberId(String memberId) throws InvalidParameterException {
        for (FederationUser member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        
        throw new InvalidParameterException(
                String.format(Messages.Exception.MEMBER_NOT_FOUND_IN_FEDERATION, memberId, this.id));
    }

    public String getOwner() {
        return owner;
    }
    
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean enabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public List<FederationUser> getMemberList() {
        return members;
    }

    public void registerService(FederationService service) {
        this.services.add(service);
    }

    public List<FederationService> getServices() {
        return this.services;
    }

    public FederationService getService(String serviceId) throws InvalidParameterException {
        for (FederationService service : this.services) {
            if (service.getServiceId().equals(serviceId)) {
                return service;
            }
        }
        
        throw new InvalidParameterException(
                String.format(Messages.Log.CANNOT_FIND_SERVICE, serviceId));
    }
    
    public List<FederationService> getAuthorizedServices(String userId) throws InvalidParameterException {
        List<FederationService> authorizedServices = new ArrayList<FederationService>();
        FederationUser user = getUserById(userId);
        
        for (FederationService service : this.services) {
            if (service.isDiscoverableBy(user)) {
                authorizedServices.add(service);
            }
        }
        
        return authorizedServices;
    }
    
    // TODO test
    public String createAttribute(String attributeName) {
        FederationAttribute newAttribute = new FederationAttribute(attributeName);
        this.attributes.add(newAttribute);
        return newAttribute.getId();
    }

    public List<FederationAttribute> getAttributes() {
        return this.attributes;
    }

    // TODO test
    public void grantAttribute(String memberId, String attributeId) throws InvalidParameterException {
        // FIXME check if attribute exists
        FederationUser user = getUserByMemberId(memberId);
        user.addAttribute(attributeId);
    }
    
    // TODO test
    public void revokeAttribute(String memberId, String attributeId) throws InvalidParameterException {
        FederationUser user = getUserByMemberId(memberId);
        user.removeAttribute(attributeId);
    }

    // TODO test
    public boolean isServiceOwner(String requester) throws InvalidParameterException {
        FederationUser user = getUserById(requester);
        return user.getAttributes().contains(this.serviceOwnerAttribute.getId());
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
}
