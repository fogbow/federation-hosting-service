package cloud.fogbow.fhs.core;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import cloud.fogbow.fhs.api.http.response.AttributeDescription;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationInfo;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

public class ApplicationFacade {

    private static ApplicationFacade instance;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    private List<FederationUser> fhsOperators;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;  
    
    public static ApplicationFacade getInstance() {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new ApplicationFacade();
            }
            return instance;
        }
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
    
    /*
     * 
     * Federations
     * 
     */
    
    public FederationId createFederation(String userToken, String name, Map<String, String> metadata, String description, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_FEDERATION));
        Federation createdFederation = this.federationHost.createFederation(requestUser.getId(), name, metadata, description, enabled);
        return new FederationId(createdFederation.getName(), createdFederation.getId(), createdFederation.enabled());
    }
    
    public List<FederationDescription> listFederations(String userToken, String owner) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_FEDERATIONS));
        List<Federation> federationList = this.federationHost.getFederationsOwnedByUser(requestUser.getId());
        List<FederationDescription> federationDescriptions = new ArrayList<FederationDescription>();
        for (Federation federation : federationList) {
            String id = federation.getId();
            String name = federation.getName();
            String description = federation.getDescription();
            
            federationDescriptions.add(new FederationDescription(id, name, description));
        }
        
        return federationDescriptions;
    }

    public FederationInfo getFederationInfo(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_FEDERATION_INFO));
        Federation federation = this.federationHost.getFederation(requestUser.getId(), federationId);
        return new FederationInfo(federationId, federation.getName(), federation.getMemberList().size(), 
                federation.getServices().size());
    }

    public void deleteFederation(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_FEDERATION));
        this.federationHost.deleteFederation(requestUser.getId(), federationId);
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
        FederationUser user = this.federationHost.grantMembership(requestUser.getId(), federationId, userId, email, 
                description, authenticationProperties);
        return new MemberId(user.getMemberId());
    }
    
    public List<FederationMember> listMembers(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_MEMBERS));
        List<FederationUser> members = this.federationHost.getFederationMembers(requestUser.getId(), federationId);
        List<FederationMember> memberIds = new ArrayList<FederationMember>();
        
        for (FederationUser member : members) {
            memberIds.add(new FederationMember(member.getMemberId(), member.getName(), 
                    member.getEmail(), member.getDescription(), member.isEnabled(), member.getAttributes()));
        }
        
        return memberIds;
    }

    public FederationMember getMemberInfo(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_MEMBER_INFO));
        FederationUser member = this.federationHost.getFederationMemberInfo(requestUser.getId(), federationId, memberId);
        return new FederationMember(member.getMemberId(), member.getName(), 
                member.getEmail(), member.getDescription(), member.isEnabled(), member.getAttributes());
    }

    public void revokeMembership(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REVOKE_MEMBERSHIP));
        this.federationHost.revokeMembership(requestUser.getId(), federationId, memberId);
    }
    
    /*
     * 
     * Attributes
     * 
     */
    
    public AttributeDescription createAttribute(String userToken, String federationId, String attributeName) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_ATTRIBUTE));
        String attributeId = this.federationHost.createAttribute(requestUser.getId(), federationId, attributeName);
        return new AttributeDescription(attributeId, attributeName);
    }

    public List<AttributeDescription> getFederationAttributes(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_ATTRIBUTES));
        List<FederationAttribute> attributes = this.federationHost.getFederationAttributes(requestUser.getId(), federationId);
        List<AttributeDescription> attributesDescriptions = new ArrayList<AttributeDescription>();
        
        for (FederationAttribute attribute : attributes) {
            attributesDescriptions.add(new AttributeDescription(attribute.getId(), attribute.getName()));
        }
        
        return attributesDescriptions;
    }

    public void deleteFederationAttribute(String userToken, String federationId, String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_ATTRIBUTE));
        this.federationHost.deleteAttribute(requestUser.getId(), federationId, attributeId);
    }

    public void grantAttribute(String userToken, String federationId, String memberId,
            String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GRANT_ATTRIBUTE));
        this.federationHost.grantAttribute(requestUser.getId(), federationId, memberId, attributeId);
    }
    
    public void revokeAttribute(String userToken, String federationId, String memberId, String attributeId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REVOKE_ATTRIBUTE));
        this.federationHost.revokeAttribute(requestUser.getId(), federationId, memberId, attributeId);
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
        String serviceId = this.federationHost.registerService(requestUser.getId(), federationId, endpoint, metadata, discoveryPolicy, accessPolicy);
        return new ServiceId(serviceId);
    }

    public List<ServiceId> getServices(String userToken, String federationId, String ownerId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICES));
        List<String> servicesIds = this.federationHost.getOwnedServices(requestUser.getId(), federationId);
        List<ServiceId> services = new ArrayList<ServiceId>();
        
        for (String serviceId : servicesIds) {
            services.add(new ServiceId(serviceId));
        }
        
        return services;
    }
    
    public ServiceInfo getService(String userToken, String federationId, String ownerId, String serviceId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICE));
        
        FederationService service = this.federationHost.getOwnedService(requestUser.getId(), federationId, serviceId);
        return new ServiceInfo(service.getServiceId(), service.getEndpoint(), service.getMetadata(), 
                service.getDiscoveryPolicy().getName(), service.getInvoker().getName());
    }

    public void updateService(String userToken, String federationId, String ownerId, String serviceId,
            Map<String, String> metadata, String discoveryPolicy, String accessPolicy) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.UPDATE_SERVICE));
        this.federationHost.updateService(requestUser.getId(), federationId, ownerId, serviceId, 
                metadata, discoveryPolicy, accessPolicy);
    }

    public void deleteService(String userToken, String federationId, String ownerId, String serviceId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DELETE_SERVICE));
        this.federationHost.deleteService(requestUser.getId(), federationId, ownerId, serviceId);        
    }

    public List<ServiceDiscovered> discoverServices(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DISCOVER_SERVICES));
        
        List<FederationService> services = this.federationHost.getAuthorizedServices(requestUser.getId(), federationId);
        List<ServiceDiscovered> discoveredService = new ArrayList<ServiceDiscovered>();
        
        for (FederationService service : services) {
            discoveredService.add(new ServiceDiscovered(service.getServiceId(), service.getMetadata(), service.getEndpoint()));
        }
        
        return discoveredService;
    }
    
    public RequestResponse invocation(String userToken, String federationId, String serviceId, HttpMethod method,
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.INVOKE));
        
        ServiceResponse response = this.federationHost.invokeService(requestUser.getId(), federationId, serviceId, method, path, headers, body);
        return new RequestResponse(response.getCode(), response.getResponse());
    }
    
    /*
     * 
     * Authentication
     * 
     */

    public String login(String federationId, String memberId, Map<String, String> credentials)
            throws InvalidParameterException, UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        return this.federationHost.login(federationId, memberId, credentials);
    }
    
    public String operatorLogin(String operatorId, Map<String, String> credentials) throws UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException, InvalidParameterException {
        FederationUser fhsOperator = getUserByName(operatorId);
        
        if (fhsOperator != null) {
            String identityPluginClassName = fhsOperator.getIdentityPluginClassName();
            Map<String, String> identityPluginProperties = fhsOperator.getIdentityPluginProperties(); 
            FederationAuthenticationPlugin authenticationPlugin = this.authenticationPluginInstantiator.getAuthenticationPlugin(
                    identityPluginClassName, identityPluginProperties);
            return authenticationPlugin.authenticate(credentials);
        }
        
        throw new InvalidParameterException(String.format(Messages.Exception.INVALID_FHS_OPERATOR_ID, operatorId));
    }

    public String federationAdminLogin(String adminId, Map<String, String> credentials) throws InvalidParameterException, 
    UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        return this.federationHost.federationAdminLogin(adminId, credentials);
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
        return this.federationHost.map(federationId, serviceId, userId, cloudName);
    }
    
    private SystemUser authenticate(String userToken) throws FogbowException {
        RSAPublicKey publicKey = ServiceAsymmetricKeysHolder.getInstance().getPublicKey();
        return AuthenticationUtil.authenticate(publicKey, userToken);
    }

    public String getPublicKey() throws InternalServerErrorException, GeneralSecurityException {
        return CryptoUtil.toBase64(ServiceAsymmetricKeysHolder.getInstance().getPublicKey());
    }
}
