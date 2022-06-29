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

import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cloud.fogbow.as.constants.ConfigurationPropertyKeys;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

@Entity
@Table(name = "federation_table")
public class Federation {
    @Transient
    private final Logger LOGGER = Logger.getLogger(Federation.class);
    
    private static final String FEDERATION_ID_COLUMN_NAME = "federation_id";
    private static final String FEDERATION_OWNER_COLUMN_NAME = "federation_owner";
    private static final String FEDERATION_NAME_COLUMN_NAME = "federation_name";
    private static final String FEDERATION_DESCRIPTION_COLUMN_NAME = "federation_description";
    private static final String FEDERATION_ENABLED_COLUMN_NAME = "federation_enabled";
    private static final String FEDERATION_MEMBERS_COLUMN_NAME = "federation_members";
    private static final String FEDERATION_SERVICES_COLUMN_NAME = "federation_services";
    private static final String FEDERATION_ATTRIBUTES_COLUMN_NAME = "federation_attributes";
    private static final String FEDERATION_ALLOWED_REMOTE_FED_ADMINS_COLUMN_NAME = "federation_allowed_remote_fed_admins";
    private static final String FEDERATION_METADATA_COLUMN_NAME_COLUMN_NAME = "federation_metadata";
    private static final String FEDERATION_REMOTE_ADMINS_COLUMN_NAME = "federation_remote_admins";

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
    
    // TODO add column name
    private String fhsId;
    
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
    
    @Column(name = FEDERATION_ALLOWED_REMOTE_FED_ADMINS_COLUMN_NAME)
    @OneToMany(cascade={CascadeType.ALL})
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RemoteFederationUser> allowedFedAdmins;
    
    @Column(name = FEDERATION_REMOTE_ADMINS_COLUMN_NAME)
    @OneToMany(cascade={CascadeType.ALL})
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FederationUser> remoteAdmins;

    @Column(name = FEDERATION_METADATA_COLUMN_NAME_COLUMN_NAME)
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
        this(UUID.randomUUID().toString(), owner, name, 
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY),
                metadata, description, enabled);
    }
    
    public Federation(String id, String owner, String name, String fhsId, Map<String, String> metadata, 
            String description, boolean enabled) {
        this(id, owner, name, fhsId, metadata, description, enabled, new ArrayList<FederationUser>(), 
                new ArrayList<RemoteFederationUser>(), new ArrayList<FederationUser>(), new ArrayList<FederationService>(), 
                new ArrayList<FederationAttribute>(), new FederationAuthenticationPluginInstantiator(),
                new FederationServiceFactory());
    }
    
    public Federation(String id, String owner, String name, String fhsId, 
            Map<String, String> metadata, String description, boolean enabled, 
            List<FederationUser> members, List<RemoteFederationUser> allowedFedAdmins, 
            List<FederationUser> remoteAdmins, List<FederationService> services, 
            List<FederationAttribute> attributes, FederationAuthenticationPluginInstantiator authenticationPluginInstantiator, 
            FederationServiceFactory federationServiceFactory) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.fhsId = fhsId;
        this.metadata = metadata;
        this.description = description;
        this.enabled = enabled;
        this.allowedFedAdmins = allowedFedAdmins;
        this.remoteAdmins = remoteAdmins;
        this.members = members;
        this.services = services;
        this.attributes = attributes;
        this.authenticationPluginInstantiator = authenticationPluginInstantiator;
        this.federationServiceFactory = federationServiceFactory;
    }

    public FederationUser addUser(String userId, String email, String description, Map<String, String> authenticationProperties) throws InvalidParameterException {
        FederationUser newMember = new FederationUser(userId, this.id, this.fhsId, email, description, true, 
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
    
    public boolean isFederationOwner(String requester) {
        // FIXME should check for other fed admins 
        // in the federation
        return this.owner.equals(requester);
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

    public String getFhsId() {
        return fhsId;
    }
    
    public List<FederationUser> getRemoteAdmins() {
        return remoteAdmins;
    }
    
    public List<FederationUser> getMemberList() {
        return members;
    }

    public String registerService(String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, Map<String, String> metadata) throws InvalidParameterException {
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
    
    // TODO test
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
    
    // TODO test
    public ServiceResponse invoke(String requester, String federationId, String serviceId, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        FederationService service = getService(serviceId);
        ServiceAccessPolicy accessPolicy = service.getAccessPolicy();
        FederationUser federationUser = getUserById(requester);
        ServiceOperation operation = new ServiceOperation(method);
        
        if (accessPolicy.isAllowedToPerform(federationUser, operation)) {
            return service.invoke(federationUser, method, path, headers, body);
        }
        
        throw new UnauthorizedRequestException(Messages.Exception.USER_IS_NOT_AUTHORIZED_TO_PERFORM_OPERATION_ON_SERVICE);
    }

    // TODO test
    // TODO validation
    public void addRemoteUserAsAllowedFedAdmin(String fedAdminId, String fhsId) {
        this.allowedFedAdmins.add(new RemoteFederationUser(fedAdminId, fhsId));
    }

    // TODO test
    // TODO validation
    public void removeRemoteUserFromAllowedAdmins(String fedAdminId, String fhsId) {
        this.allowedFedAdmins.remove(new RemoteFederationUser(fedAdminId, fhsId));
    }

    public List<RemoteFederationUser> getAllowedRemoteJoins() {
        return new ArrayList<RemoteFederationUser>(this.allowedFedAdmins);
    }

    // TODO test
    // TODO validation
    public void addRemoteAdmin(FederationUser requester, String fhsId) {
        if (this.allowedFedAdmins.contains(new RemoteFederationUser(requester.getName(), fhsId))) {
            this.remoteAdmins.add(requester);
        }
    }
    
    public static String toJson(Federation federation) {
        Gson gson = new Gson();
        
        String federationEnabledStr = gson.toJson(federation.enabled());
        String federationMembersStr = gson.toJson(federation.getMemberList());
        String federationServicesStr = gson.toJson(federation.getServices());
        String federationAttributesStr = gson.toJson(federation.attributes);
        String federationAllowedRemoteJoinsStr = gson.toJson(federation.getAllowedRemoteJoins());
        String federationRemoteAdminsStr = gson.toJson(federation.getRemoteAdmins());
        String federationMetadataStr = gson.toJson(federation.getMetadata());
        
        // TODO constant
        return String.join("#", federation.getId(), federation.getOwner(), federation.getName(), 
                federation.getFhsId(), federation.getDescription(), federationEnabledStr, federationMembersStr, 
                federationServicesStr, federationAttributesStr, federationAllowedRemoteJoinsStr,
                federationRemoteAdminsStr, federationMetadataStr);
    }
    
    public static Federation fromJson(String json) {
        // TODO constant
        String[] fields = json.split("#");
        Gson gson = new Gson();
        
        String federationId = fields[0];
        String federationOwner = fields[1];
        String federationName = fields[2];
        String federationFhs = fields[3];
        String federationDescription = fields[4];
        String federationEnabledStr = fields[5];
        String federationMembersStr = fields[6];
        String federationServicesStr = fields[7];
        String federationAttributesStr = fields[8];
        String federationAllowedRemoteJoinsStr = fields[9];
        String federationRemoteAdminsStr = fields[10];
        String federationMetadataStr = fields[11];
        
        Boolean federationEnabled = gson.fromJson(federationEnabledStr, Boolean.class);
        List<FederationUser> federationMembers = gson.fromJson(federationMembersStr, 
                new TypeToken<List<FederationUser>>(){}.getType());
        List<FederationService> federationServices = gson.fromJson(federationServicesStr, 
                new TypeToken<List<FederationService>>(){}.getType());
        List<FederationAttribute> federationAttributes = gson.fromJson(federationAttributesStr, 
                new TypeToken<List<FederationAttribute>>(){}.getType());
        List<RemoteFederationUser> federationAllowedRemoteJoins = gson.fromJson(federationAllowedRemoteJoinsStr, 
                new TypeToken<List<RemoteFederationUser>>(){}.getType());
        List<FederationUser> federationRemoteAdmins = gson.fromJson(federationRemoteAdminsStr, 
                new TypeToken<List<FederationUser>>(){}.getType());
        Map<String, String> federationMetadata = gson.fromJson(federationMetadataStr, 
                new TypeToken<Map<String, String>>(){}.getType());
        
        return new Federation(federationId, federationOwner, federationName, federationFhs, 
                federationMetadata, federationDescription, federationEnabled, federationMembers, 
                federationAllowedRemoteJoins, federationRemoteAdmins, federationServices, federationAttributes,
                new FederationAuthenticationPluginInstantiator(), new FederationServiceFactory());
    }
}
