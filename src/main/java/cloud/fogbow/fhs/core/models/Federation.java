package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;

public class Federation {
    private String id;
    private String owner;
    private String name;
    private String description;
    private boolean enabled;
    private List<FederationUser> members;
    private List<FederationService> services;
    private Map<String, String> metadata;
    
    public Federation(String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(UUID.randomUUID().toString(), owner, name, metadata, description, enabled);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(id, owner, name, metadata, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<FederationService>());
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled, 
            List<FederationUser> members, List<FederationService> services) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.metadata = metadata;
        this.description = description;
        this.enabled = enabled;
        this.members = members;
        this.services = services;
    }

    // FIXME should receive email and description
    public FederationUser addUser(String userId) {
        FederationUser newMember = new FederationUser(userId, "", "", true);
        this.members.add(newMember);
        return newMember;
    }

    public FederationUser getUser(String memberId) throws InvalidParameterException {
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
        
        // TODO add message
        throw new InvalidParameterException();
    }
    
    public List<FederationService> getAuthorizedServices(String memberId) throws InvalidParameterException {
        List<FederationService> authorizedServices = new ArrayList<FederationService>();
        FederationUser user = getUser(memberId);
        
        for (FederationService service : this.services) {
            if (service.isDiscoverableBy(user)) {
                authorizedServices.add(service);
            }
        }
        
        return authorizedServices;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
