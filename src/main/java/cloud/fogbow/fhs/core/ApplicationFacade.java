package cloud.fogbow.fhs.core;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.NotImplementedOperationException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.CryptoUtil;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.api.http.response.AllowedRemoteJoin;
import cloud.fogbow.fhs.api.http.response.AttributeDescription;
import cloud.fogbow.fhs.api.http.response.FedAdminInfo;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationInfo;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;
import cloud.fogbow.fhs.core.models.RemoteFederation;
import cloud.fogbow.fhs.core.models.RemoteFederationUser;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.SynchronizationManager;

public class ApplicationFacade {
    public static final String PROPERTY_NAME_OPERATOR_ID_SEPARATOR = "_";
    
    private static ApplicationFacade instance;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    private List<FederationUser> fhsOperators;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;  
    private DatabaseManager databaseManager;
    private SynchronizationManager synchronizationManager;
    private FhsCommunicationMechanism fhsCommunicationMechanism;
    
    public static ApplicationFacade getInstance() {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new ApplicationFacade();
            }
            return instance;
        }
    }
    
    private ApplicationFacade() {
        
    }
    
    public static List<FederationUser> loadFhsOperatorsOrFail() throws ConfigurationErrorException {
        String operatorIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY);
        
        if (operatorIdsListString == null || operatorIdsListString.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.NO_OPERATOR_ID_SPECIFIED);
        } else {
            return loadFhsOperators(operatorIdsListString);
        }
    }

    private static List<FederationUser> loadFhsOperators(String operatorIdsListString) {
        List<FederationUser> fhsOperators = new ArrayList<>();
        List<String> fhsOperatorUserIds = Arrays.asList(operatorIdsListString.split(
                SystemConstants.OPERATOR_IDS_SEPARATOR));
        
        for (String fhsOperatorUserId : fhsOperatorUserIds) {
            FederationUser operator = loadOperator(fhsOperatorUserId); 
            fhsOperators.add(operator);
        }
        
        return fhsOperators;
    }
    
    private static FederationUser loadOperator(String fhsOperatorUserId) {
        Map<String, String> fhsOperatorAuthenticationProperties = new HashMap<String, String>();
        Properties properties = PropertiesHolder.getInstance().getProperties();
        
        for (Object keyProperties : properties.keySet()) {
            String keyPropertiesStr = keyProperties.toString();
            if (keyPropertiesStr.startsWith(fhsOperatorUserId + PROPERTY_NAME_OPERATOR_ID_SEPARATOR)) {
                String value = properties.getProperty(keyPropertiesStr);
                String key = normalizeKeyProperties(fhsOperatorUserId, keyPropertiesStr);
                fhsOperatorAuthenticationProperties.put(key, value);
            }
        }

        return new FederationUser(fhsOperatorUserId, "", "", PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY), 
                "", true, fhsOperatorAuthenticationProperties, true, false);
    }
    
    private static String normalizeKeyProperties(String fhsOperatorUserId, String keyPropertiesStr) {
        return keyPropertiesStr.replace(fhsOperatorUserId + PROPERTY_NAME_OPERATOR_ID_SEPARATOR, "");
    }

    public void setAuthorizationPlugin(AuthorizationPlugin<FhsOperation> authorizationPlugin) {
        this.authorizationPlugin = authorizationPlugin;
    }
    
    public void setLocalFederationHost(FederationHost localFederationHost) {
        this.federationHost = localFederationHost;
    }
    
    public void setFhsOperators(List<FederationUser> fhsOperators) {
        this.fhsOperators = fhsOperators;
    }
    
    public void setAuthenticationPluginInstantiator(
            FederationAuthenticationPluginInstantiator authenticationPluginInstantiator) {
        this.authenticationPluginInstantiator = authenticationPluginInstantiator;
    }
    
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void setSynchronizationManager(SynchronizationManager synchronizationManager) {
        this.synchronizationManager = synchronizationManager;
    }
    
    public void setFhsCommunicationMechanism(FhsCommunicationMechanism fhsCommunicationMechanism) {
        this.fhsCommunicationMechanism = fhsCommunicationMechanism;
    }
    
    /*
     * 
     * FHSOperator
     * 
     */
    
    public String addFederationAdmin(String userToken, String adminName, String adminEmail, 
            String adminDescription, boolean enabled, Map<String, String> authenticationProperties) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.ADD_FED_ADMIN));
        return this.federationHost.addFederationAdmin(adminName, adminEmail, adminDescription, enabled, authenticationProperties);
    }

    public List<FedAdminInfo> getFederationAdmins(String userToken) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_FED_ADMINS));
        
        List<FederationUser> federationAdmins = this.federationHost.getFederationAdmins();
        List<FedAdminInfo> adminsInfo = new ArrayList<FedAdminInfo>();
        
        for (FederationUser admin : federationAdmins) {
            String adminName = admin.getName();
            List<Federation> federations = this.federationHost.getFederationsInstancesOwnedByAnotherMember(adminName);
            List<String> federationsIds = new ArrayList<String>();
            
            for (Federation federation : federations) {
                federationsIds.add(federation.getId());
            }
            
            adminsInfo.add(new FedAdminInfo(admin.getMemberId(), admin.getName(), admin.getEmail(), admin.getDescription(), 
                    admin.isEnabled(), federationsIds));
        }
        
        return adminsInfo;
    }

    public void updateFederationAdmin(String userToken, String adminId, String name, String email,
            String description, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.UPDATE_FED_ADMIN));
        this.federationHost.updateFederationAdmin(adminId, name, email, description, enabled);
    }

    public void deleteFederationAdmin(String userToken, String adminId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_FED_ADMIN));
        this.federationHost.deleteFederationAdmin(adminId);
    }

    public List<FederationInstance> listFederationInstances(String userToken) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_FEDERATION_INSTANCES));
        List<Federation> federations = this.federationHost.getFederations();
        List<FederationInstance> federationInstances = new ArrayList<FederationInstance>();
        
        for (Federation federation : federations) {
            federationInstances.add(new FederationInstance(federation.getId(), federation.getName(), federation.getDescription(), 
                    federation.enabled(), federation.getOwner()));
        }
        
        return federationInstances;
    }
    
    public void updateFederation(String userToken, String federationId, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.UPDATE_FEDERATION));
        this.federationHost.updateFederation(federationId, enabled);
    }

    public void deleteFederationInstance(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_FEDERATION_INSTANCE));
        this.federationHost.deleteFederationInstance(federationId);
    }
    
    // TODO test
    public void reload(String userToken) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.RELOAD_CONFIGURATION));
        
        synchronizationManager.setAsReloading();
        
        try {
            synchronizationManager.waitForRequests();
            
            PropertiesHolder.reset();
            
            FhsPublicKeysHolder.reset();
            
            this.federationHost.reload(databaseManager, fhsCommunicationMechanism);
            
            String publicKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PUBLIC_KEY_FILE_PATH);
            String privateKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PRIVATE_KEY_FILE_PATH);
            ServiceAsymmetricKeysHolder.reset(publicKeyFilePath, privateKeyFilePath);
            
            String className = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY);
            AuthorizationPlugin<FhsOperation> authorizationPlugin = AuthorizationPluginInstantiator.getAuthorizationPlugin(className);
            setAuthorizationPlugin(authorizationPlugin);
            
            setFhsOperators(ApplicationFacade.loadFhsOperatorsOrFail());
        } finally {
            synchronizationManager.finishReloading();
        }
    }
    
    /*
     * 
     * Federations
     * 
     */
    
    public FederationId createFederation(String userToken, String name, Map<String, String> metadata, String description, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_FEDERATION));
        
        synchronizationManager.startOperation();
        
        try {
            Federation createdFederation = this.federationHost.createFederation(requestUser.getId(), name, metadata, description, enabled);
            return new FederationId(createdFederation.getName(), createdFederation.getId(), createdFederation.enabled());
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    // TODO update test
    public List<FederationDescription> listFederations(String userToken, String owner) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_FEDERATIONS));
        
        synchronizationManager.startOperation();
        
        try {
            List<Federation> federationList = this.federationHost.getFederationsOwnedByUser(requestUser.getId());
            List<Federation> remoteFederationList = this.federationHost.getAdminRemoteFederations(requestUser.getId());
            federationList.addAll(remoteFederationList);
            List<FederationDescription> federationDescriptions = new ArrayList<FederationDescription>();
            for (Federation federation : federationList) {
                String id = federation.getId();
                String name = federation.getName();
                String description = federation.getDescription();
                
                federationDescriptions.add(new FederationDescription(id, name, description));
            }
            
            return federationDescriptions;    
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public FederationInfo getFederationInfo(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_FEDERATION_INFO));
        
        synchronizationManager.startOperation();
        
        try {
            Federation federation = this.federationHost.getFederation(requestUser.getId(), federationId);
            return new FederationInfo(federationId, federation.getName(), federation.getMemberList().size(), 
                    federation.getServices().size());
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void deleteFederation(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_FEDERATION));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.deleteFederation(requestUser.getId(), federationId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public List<FederationDescription> getRemoteFederationList(String userToken) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_REMOTE_FEDERATION_LIST));
        
        synchronizationManager.startOperation();
        
        try {
            List<RemoteFederation> federationInstances = this.federationHost.getRemoteFederationList(requestUser.getId());
            List<FederationDescription> federationDescriptions = new ArrayList<FederationDescription>();
            for (RemoteFederation federationInstance : federationInstances) {
                String id = federationInstance.getFedId();
                String name = federationInstance.getFedName();
                String description = federationInstance.getDescription();
                
                federationDescriptions.add(new FederationDescription(id, name, description));
            }
            
            return federationDescriptions;    
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void joinRemoteFederation(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.JOIN_REMOTE_FEDERATION));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.requestToJoinRemoteFederation(requestUser.getId(), federationId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    // TODO test
    public List<AllowedRemoteJoin> getRemoteUsersAllowedAdmins(String userToken) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_REMOTE_USERS_ALLOWED_ADMINS));
        
        synchronizationManager.startOperation();
        
        try {
            List<AllowedRemoteJoin> allowedRemoteJoins = new ArrayList<AllowedRemoteJoin>();
            List<Federation> federations = federationHost.getFederationsOwnedByUser(requestUser.getId());
            
            for (Federation federation : federations) {
                List<RemoteFederationUser> allowedRemoteJoinsForFederation = federation.getAllowedRemoteJoins();
                
                for (RemoteFederationUser allowedRemoteJoin : allowedRemoteJoinsForFederation) {
                    allowedRemoteJoins.add(new AllowedRemoteJoin(federation.getId(), federation.getName(), 
                            allowedRemoteJoin.getFedAdminId(), allowedRemoteJoin.getFhsId()));
                }
            }
            
            return allowedRemoteJoins;
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public void addRemoteUserToAllowedAdmins(String userToken, String remoteFedAdminId, String fhsId,
            String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.ADD_REMOTE_USER_TO_ALLOWED_ADMINS));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.addUserToAllowedAdmins(requestUser.getId(), remoteFedAdminId, fhsId, federationId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public void removeRemoteUserFromAllowedAdmins(String userToken, String remoteFedAdminId, String fhsId,
            String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REMOVE_REMOTE_USER_FROM_ALLOWED_ADMINS));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.removeUserFromAllowedAdmins(requestUser.getId(), remoteFedAdminId, fhsId, federationId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    /*
     * 
     * Membership
     * 
     */
    
    public MemberId grantMembership(String userToken, String federationId, String userId, 
            String email, String description, Map<String, String> authenticationProperties) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GRANT_MEMBERSHIP));
        
        synchronizationManager.startOperation();
        
        try {
            FederationUser user = this.federationHost.grantMembership(requestUser.getId(), federationId, userId, email, 
                    description, authenticationProperties);
            return new MemberId(user.getMemberId());
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public List<FederationMember> listMembers(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_MEMBERS));
        
        synchronizationManager.startOperation();
        
        try {
            List<FederationUser> members = this.federationHost.getFederationMembers(requestUser.getId(), federationId);
            List<FederationMember> memberIds = new ArrayList<FederationMember>();
            
            for (FederationUser member : members) {
                memberIds.add(new FederationMember(member.getMemberId(), member.getName(), 
                        member.getEmail(), member.getDescription(), member.isEnabled(), member.getAttributes()));
            }
            
            return memberIds;
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public FederationMember getMemberInfo(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_MEMBER_INFO));
        
        synchronizationManager.startOperation();
        
        try {
            FederationUser member = this.federationHost.getFederationMemberInfo(requestUser.getId(), federationId, memberId);
            return new FederationMember(member.getMemberId(), member.getName(), 
                    member.getEmail(), member.getDescription(), member.isEnabled(), member.getAttributes());
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void revokeMembership(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REVOKE_MEMBERSHIP));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.revokeMembership(requestUser.getId(), federationId, memberId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    /*
     * 
     * Attributes
     * 
     */
    
    public AttributeDescription createAttribute(String userToken, String federationId, String attributeName) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_ATTRIBUTE));
        
        synchronizationManager.startOperation();
        
        try {
            String attributeId = this.federationHost.createAttribute(requestUser.getId(), federationId, attributeName);
            return new AttributeDescription(attributeId, attributeName);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public List<AttributeDescription> getFederationAttributes(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_ATTRIBUTES));
        
        synchronizationManager.startOperation();
        
        try {
            List<FederationAttribute> attributes = this.federationHost.getFederationAttributes(requestUser.getId(), federationId);
            List<AttributeDescription> attributesDescriptions = new ArrayList<AttributeDescription>();
            
            for (FederationAttribute attribute : attributes) {
                attributesDescriptions.add(new AttributeDescription(attribute.getId(), attribute.getName()));
            }
            
            return attributesDescriptions;
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void deleteFederationAttribute(String userToken, String federationId, String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_ATTRIBUTE));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.deleteAttribute(requestUser.getId(), federationId, attributeId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void grantAttribute(String userToken, String federationId, String memberId,
            String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GRANT_ATTRIBUTE));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.grantAttribute(requestUser.getId(), federationId, memberId, attributeId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public void revokeAttribute(String userToken, String federationId, String memberId, String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REVOKE_ATTRIBUTE));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.revokeAttribute(requestUser.getId(), federationId, memberId, attributeId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    /*
     * 
     * Services
     * 
     */
    
    public ServiceId registerService(String userToken, String federationId, String ownerId, String endpoint,
            Map<String, String> metadata, String discoveryPolicy, String accessPolicy) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REGISTER_SERVICE));
        
        synchronizationManager.startOperation();
        
        try {
            String serviceId = this.federationHost.registerService(requestUser.getId(), federationId, endpoint, metadata, discoveryPolicy, accessPolicy);
            return new ServiceId(serviceId);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public List<ServiceId> getServices(String userToken, String federationId, String ownerId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICES));
        
        synchronizationManager.startOperation();
        
        try {
            List<String> servicesIds = this.federationHost.getOwnedServices(requestUser.getId(), federationId);
            List<ServiceId> services = new ArrayList<ServiceId>();
            
            for (String serviceId : servicesIds) {
                services.add(new ServiceId(serviceId));
            }
            
            return services;
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public ServiceInfo getService(String userToken, String federationId, String ownerId, String serviceId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICE));
        
        synchronizationManager.startOperation();
        
        try {
            FederationService service = this.federationHost.getOwnedService(requestUser.getId(), federationId, serviceId);
            return new ServiceInfo(service.getServiceId(), service.getEndpoint(), service.getMetadata(), 
                    service.getDiscoveryPolicy().getName(), service.getInvoker().getName());
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void updateService(String userToken, String federationId, String ownerId, String serviceId,
            Map<String, String> metadata, String discoveryPolicy, String accessPolicy) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.UPDATE_SERVICE));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.updateService(requestUser.getId(), federationId, ownerId, serviceId, 
                    metadata, discoveryPolicy, accessPolicy);
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public void deleteService(String userToken, String federationId, String ownerId, String serviceId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_SERVICE));
        
        synchronizationManager.startOperation();
        
        try {
            this.federationHost.deleteService(requestUser.getId(), federationId, ownerId, serviceId);        
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public List<ServiceDiscovered> discoverServices(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DISCOVER_SERVICES));
        
        synchronizationManager.startOperation();
        
        try {
            List<FederationService> services = this.federationHost.getAuthorizedServices(requestUser.getId(), federationId);
            List<ServiceDiscovered> discoveredService = new ArrayList<ServiceDiscovered>();
            
            for (FederationService service : services) {
                discoveredService.add(new ServiceDiscovered(service.getServiceId(), service.getMetadata(), service.getEndpoint()));
            }
            
            return discoveredService;
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public RequestResponse invocation(String userToken, String federationId, String serviceId, HttpMethod method,
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.INVOKE));
        
        synchronizationManager.startOperation();
        
        try {
            ServiceResponse response = this.federationHost.invokeService(requestUser.getId(), federationId, serviceId, method, path, headers, body);
            return new RequestResponse(response.getCode(), response.getResponse());
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    /*
     * 
     * Authentication
     * 
     */

    public String login(String federationId, String memberId, Map<String, String> credentials)
            throws InvalidParameterException, UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        synchronizationManager.startOperation();
        
        try {
            return this.federationHost.login(federationId, memberId, credentials);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    public String operatorLogin(String operatorId, Map<String, String> credentials) throws UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException, InvalidParameterException {
        synchronizationManager.startOperation();
        
        try {
            FederationUser fhsOperator = getUserByName(operatorId);
            
            if (fhsOperator != null) {
                String identityPluginClassName = fhsOperator.getIdentityPluginClassName();
                Map<String, String> identityPluginProperties = fhsOperator.getIdentityPluginProperties(); 
                FederationAuthenticationPlugin authenticationPlugin = this.authenticationPluginInstantiator.getAuthenticationPlugin(
                        identityPluginClassName, identityPluginProperties);
                return authenticationPlugin.authenticate(credentials);
            }
            
            throw new InvalidParameterException(String.format(Messages.Exception.INVALID_FHS_OPERATOR_ID, operatorId));
        } finally {
            synchronizationManager.finishOperation();
        }
    }

    public String federationAdminLogin(String adminId, Map<String, String> credentials) throws InvalidParameterException, 
    UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        synchronizationManager.startOperation();
        
        try {
            return this.federationHost.federationAdminLogin(adminId, credentials);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    private FederationUser getUserByName(String name) {
        for (FederationUser user : this.fhsOperators) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        
        return null;
    }

    public void logout(String loginSessionId) throws NotImplementedOperationException {
        throw new NotImplementedOperationException();
    }
    
    /*
     * 
     * Authorization
     * 
     */
    
    public Map<String, String> map(String userToken, String federationId, String serviceId, String userId, 
            String cloudName) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.MAP));
        
        synchronizationManager.startOperation();
        
        try {
            return this.federationHost.map(federationId, serviceId, userId, cloudName);
        } finally {
            synchronizationManager.finishOperation();
        }
    }
    
    private SystemUser authenticate(String userToken) throws FogbowException {
        RSAPublicKey publicKey = ServiceAsymmetricKeysHolder.getInstance().getPublicKey();
        return AuthenticationUtil.authenticate(publicKey, userToken);
    }

    public String getPublicKey() throws InternalServerErrorException, GeneralSecurityException {
        synchronizationManager.startOperation();
        
        try {
            return CryptoUtil.toBase64(ServiceAsymmetricKeysHolder.getInstance().getPublicKey());
        } finally {
            synchronizationManager.finishOperation();
        }
    }
}
