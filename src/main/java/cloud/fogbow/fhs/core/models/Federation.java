package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;

public class Federation {
    public static final String MEMBER_ATTRIBUTE_NAME = "member";
    public static final String SERVICE_OWNER_ATTRIBUTE_NAME = "serviceOwner";
    private static final FederationAttribute MEMBER_ATTRIBUTE = 
            new FederationAttribute(MEMBER_ATTRIBUTE_NAME, MEMBER_ATTRIBUTE_NAME);
    private static FederationAttribute SERVICE_OWNER_ATTRIBUTE = 
            new FederationAttribute(SERVICE_OWNER_ATTRIBUTE_NAME, SERVICE_OWNER_ATTRIBUTE_NAME);
    
    private String id;
    private String owner;
    private String name;
    private String description;
    private boolean enabled;
    private List<FederationUser> members;
    private List<FederationService> services;
    private List<FederationAttribute> attributes;
    private Map<String, String> metadata;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    
    public Federation(String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(UUID.randomUUID().toString(), owner, name, metadata, description, enabled);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(id, owner, name, metadata, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<FederationService>(), new ArrayList<FederationAttribute>(), 
                new FederationAuthenticationPluginInstantiator());
        this.attributes.add(MEMBER_ATTRIBUTE);
        this.attributes.add(SERVICE_OWNER_ATTRIBUTE);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled, 
            List<FederationUser> members, List<FederationService> services, 
            List<FederationAttribute> attributes, 
            FederationAuthenticationPluginInstantiator authenticationPluginInstantiator) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.metadata = metadata;
        this.description = description;
        this.enabled = enabled;
        this.members = members;
        this.services = services;
        this.attributes = attributes;
        this.authenticationPluginInstantiator = authenticationPluginInstantiator;
    }

    public FederationUser addUser(String userId, String email, String description, Map<String, String> authenticationProperties) throws InvalidParameterException {
        FederationUser newMember = new FederationUser(userId, this.id, email, description, true, authenticationProperties);
        newMember.addAttribute(MEMBER_ATTRIBUTE_NAME);
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
    
    // TODO test
    public void revokeMembership(String memberId) throws InvalidParameterException {
        FederationUser member = getUserByMemberId(memberId);
        this.members.remove(member);
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
    
    // TODO test
    public void deleteService(String serviceId) throws InvalidParameterException {
        FederationService service = getService(serviceId);
        this.services.remove(service);
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
    
    public String createAttribute(String attributeName) {
        FederationAttribute newAttribute = new FederationAttribute(attributeName);
        this.attributes.add(newAttribute);
        return newAttribute.getId();
    }

    public List<FederationAttribute> getAttributes() {
        return this.attributes;
    }

    // TODO test
    public void deleteAttribute(String attributeId) throws InvalidParameterException {
        FederationAttribute attribute = getAttributeById(attributeId);
        this.attributes.remove(attribute);
    }

    public void grantAttribute(String memberId, String attributeId) throws InvalidParameterException {
        checkIfAttributeExists(attributeId);
        FederationUser user = getUserByMemberId(memberId);
        user.addAttribute(attributeId);
    }

    // TODO when revoking service owner attribute should check if
    // the member has no registered services.
    public void revokeAttribute(String memberId, String attributeId) throws InvalidParameterException {
        checkIfAttributeExists(attributeId);
        FederationUser user = getUserByMemberId(memberId);
        user.removeAttribute(attributeId);
    }

    private void checkIfAttributeExists(String attributeId) throws InvalidParameterException {
        if (getAttributeById(attributeId) == null) {
            throw new InvalidParameterException(String.format(Messages.Exception.ATTRIBUTE_DOES_NOT_EXIST_IN_FEDERATION, 
                    attributeId, this.id));
        }
    }
    
    private FederationAttribute getAttributeById(String attributeId) {
        for (FederationAttribute attribute : this.attributes) {
            if (attribute.getId().equals(attributeId)) {
                return attribute;
            }
        }
        return null;
    }
    
    public boolean isServiceOwner(String requester) throws InvalidParameterException {
        FederationUser user = getUserById(requester);
        return user.getAttributes().contains(SERVICE_OWNER_ATTRIBUTE_NAME);
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String login(String memberId, Map<String, String> credentials) throws InvalidParameterException, 
    UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        FederationUser user = getUserByMemberId(memberId);
        String identityPluginClassName = user.getIdentityPluginClassName();
        Map<String, String> identityPluginProperties = user.getIdentityPluginProperties(); 
        FederationAuthenticationPlugin authenticationPlugin = authenticationPluginInstantiator.
                getAuthenticationPlugin(identityPluginClassName, identityPluginProperties);
        return authenticationPlugin.authenticate(credentials);
    }

    // TODO test
    public Map<String, String> map(String serviceId, String userId, String cloudName) throws InvalidParameterException {
        FederationService service = getService(serviceId);
        FederationUser user = getUserById(userId);
        return service.getAccessPolicy().getCredentialsForAccess(user, cloudName);
    }
}
