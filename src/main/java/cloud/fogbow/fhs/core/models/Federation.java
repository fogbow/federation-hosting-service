package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Federation {
    private String id;
    private String owner;
    private String name;
    private String description;
    private boolean enabled;
    private List<FederationUser> members;
    private List<FederationService> services;
    
    public Federation(String owner, String name, String description, boolean enabled) {
        this(UUID.randomUUID().toString(), owner, name, description, enabled);
    }
    
    public Federation(String id, String owner, String name, String description, boolean enabled) {
        this(id, owner, name, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<FederationService>());
    }
    
    public Federation(String id, String owner, String name, String description, boolean enabled, 
            List<FederationUser> members, List<FederationService> services) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.members = members;
        this.services = services;
    }

    public FederationUser addUser(String userId) {
        FederationUser newMember = new FederationUser(userId, "", "", true);
        this.members.add(newMember);
        return newMember;
    }

    public FederationUser getUser(String memberId) {
        for (FederationUser member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        
        // FIXME should throw exception
        return null;
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

    public FederationService getService(String serviceId) {
        for (FederationService service : this.services) {
            if (service.getServiceId().equals(serviceId)) {
                return service;
            }
        }
        // FIXME should throw exception
        return null;
    }
    
    public List<FederationService> getAuthorizedServices(String memberId) {
        List<FederationService> authorizedServices = new ArrayList<FederationService>();
        
        for (FederationService service : this.services) {
            if (service.getDiscoveryPolicy().isDiscoverableBy(getUser(memberId))) {
                authorizedServices.add(service);
            }
        }
        
        return authorizedServices;
    }
}
