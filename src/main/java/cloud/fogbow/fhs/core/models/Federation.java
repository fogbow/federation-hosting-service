package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;

@Entity
@Table(name = "federation_table")
public class Federation {
    private static final String FEDERATION_ID_COLUMN_NAME = "federation_id";
    private static final String FEDERATION_OWNER_COLUMN_NAME = "federation_owner";
    private static final String FEDERATION_NAME_COLUMN_NAME = "federation_name";
    private static final String FEDERATION_DESCRIPTION_COLUMN_NAME = "federation_description";
    private static final String FEDERATION_ENABLED_COLUMN_NAME = "federation_enabled";
    private static final String FEDERATION_MEMBERS_COLUMN_NAME = "federation_members";
    private static final String FEDERATION_SERVICES_COLUMN_NAME = "federation_services";
    private static final String FEDERATION_ATTRIBUTES_COLUMN_NAME = "federation_attributes";
    private static final String FEDERATION_METADATA_COLUMN_NAME = "federation_metadata";

    // TODO documentation
    public static final String MEMBER_ATTRIBUTE_NAME = "member";
    public static final String SERVICE_OWNER_ATTRIBUTE_NAME = "serviceOwner";
    private static final FederationAttribute MEMBER_ATTRIBUTE = 
            new FederationAttribute(MEMBER_ATTRIBUTE_NAME, MEMBER_ATTRIBUTE_NAME);
    private static FederationAttribute SERVICE_OWNER_ATTRIBUTE = 
            new FederationAttribute(SERVICE_OWNER_ATTRIBUTE_NAME, SERVICE_OWNER_ATTRIBUTE_NAME);
    
    @Column(name = FEDERATION_ID_COLUMN_NAME)
    @Id
    private String id;
    
    @Column(name = FEDERATION_OWNER_COLUMN_NAME)
    private String owner;
    
    @Column(name = FEDERATION_NAME_COLUMN_NAME)
    private String name;
    
    @Column(name = FEDERATION_DESCRIPTION_COLUMN_NAME)
    private String description;
    
    @Column(name = FEDERATION_ENABLED_COLUMN_NAME)
    private boolean enabled;
    
    @Column(name = FEDERATION_MEMBERS_COLUMN_NAME)
    @OneToMany(cascade={CascadeType.ALL})
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FederationUser> members;
    
    @Column(name = FEDERATION_SERVICES_COLUMN_NAME)
    @OneToMany(cascade={CascadeType.ALL})
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FederationService> services;
    
    @Column(name = FEDERATION_ATTRIBUTES_COLUMN_NAME)
    @OneToMany(cascade={CascadeType.ALL})
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FederationAttribute> attributes;
    
    @Column(name = FEDERATION_METADATA_COLUMN_NAME)
    @ElementCollection
    private Map<String, String> metadata;
    
    @Transient
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    
    @Transient
    private FederationServiceFactory federationServiceFactory;
    
    @PostLoad
    private void setUp() {
        this.authenticationPluginInstantiator = new FederationAuthenticationPluginInstantiator();
        this.federationServiceFactory = new FederationServiceFactory();
    }
    
    public Federation() {
        
    }
    
    public Federation(String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(UUID.randomUUID().toString(), owner, name, metadata, description, enabled);
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(id, owner, name, metadata, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<FederationService>(), new ArrayList<FederationAttribute>(), 
                new FederationAuthenticationPluginInstantiator(),
                new FederationServiceFactory());
    }
    
    public Federation(String id, String owner, String name, Map<String, String> metadata, 
            String description, boolean enabled, 
            List<FederationUser> members, List<FederationService> services, 
            List<FederationAttribute> attributes, 
            FederationAuthenticationPluginInstantiator authenticationPluginInstantiator, 
            FederationServiceFactory federationServiceFactory) {
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
        this.federationServiceFactory = federationServiceFactory;
    }

    public FederationUser addUser(String userId, String email, String description, Map<String, String> authenticationProperties) throws InvalidParameterException {
        FederationUser newMember = new FederationUser(userId, this.id, email, description, true, 
                authenticationProperties, false, false);
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
    
    // TODO should check if the user is a service owner and has registered services.
    // In this case, it should not allow the user removal.
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
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public List<FederationUser> getMemberList() {
        return members;
    }

    public String registerService(String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, Map<String, String> metadata) {
        FederationService service = this.federationServiceFactory.createService(ownerId, endpoint, discoveryPolicyClassName, 
                accessPolicyClassName, this.id, metadata);
        this.services.add(service);
        return service.getServiceId();
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
        List<FederationAttribute> attributes = new ArrayList<FederationAttribute>(this.attributes);
        attributes.add(0, SERVICE_OWNER_ATTRIBUTE);
        attributes.add(0, MEMBER_ATTRIBUTE);
        return attributes;
    }

    // TODO should check if the attribute to be removed is not 'Member' of 'ServiceOwner'
    // In this case, it should throw exception.
    // TODO should check if some user uses the attribute.
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
        if (Federation.MEMBER_ATTRIBUTE_NAME.equals(attributeId)) {
            return Federation.MEMBER_ATTRIBUTE;
        }
        
        if (Federation.SERVICE_OWNER_ATTRIBUTE_NAME.equals(attributeId)) {
            return Federation.SERVICE_OWNER_ATTRIBUTE;
        }
        
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

    public Map<String, String> map(String serviceId, String userId, String cloudName) throws InvalidParameterException {
        FederationService service = getService(serviceId);
        FederationUser user = getUserById(userId);
        return service.getAccessPolicy().getCredentialsForAccess(user, cloudName);
    }
}
