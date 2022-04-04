package cloud.fogbow.fhs.core;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.CryptoUtil;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.api.http.response.CloudCredentials;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;
import cloud.fogbow.fhs.core.models.ServiceResponse;

public class ApplicationFacade {

    private static ApplicationFacade instance;
    private RSAPublicKey asPublicKey;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    
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
    
    public String addFederationAdmin(String userToken, String adminName, String adminEmail, 
            String adminDescription, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.ADD_FED_ADMIN));
        return this.federationHost.addFederationAdmin(adminName, adminEmail, adminDescription, enabled);
    }
    
    public FederationId createFederation(String userToken, String name, Map<String, String> metadata, String description, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_FEDERATION));
        Federation createdFederation = this.federationHost.createFederation(requestUser.getId(), name, metadata, description, enabled);
        return new FederationId(createdFederation.getName(), createdFederation.getId(), createdFederation.enabled());
    }
    
    public List<FederationDescription> listFederations(String userToken, String owner) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_FEDERATIONS));
        List<Federation> federationList = this.federationHost.getFederationsOwnedByUser(requestUser.getId(), owner);
        List<FederationDescription> federationDescriptions = new ArrayList<FederationDescription>();
        for (Federation federation : federationList) {
            String id = federation.getId();
            String name = federation.getName();
            String description = federation.getDescription();
            
            federationDescriptions.add(new FederationDescription(id, name, description));
        }
        
        return federationDescriptions;
    }

    public MemberId grantMembership(String userToken, String federationId, String userId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GRANT_MEMBERSHIP));
        FederationUser user = this.federationHost.grantMembership(requestUser.getId(), federationId, userId);
        return new MemberId(user.getMemberId());
    }
    
    public List<FederationMember> listMembers(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_MEMBERS));
        List<FederationUser> members = this.federationHost.getFederationMembers(requestUser.getId(), federationId);
        List<FederationMember> memberIds = new ArrayList<FederationMember>();
        
        for (FederationUser member : members) {
            memberIds.add(new FederationMember(member.getMemberId(), member.getName(), 
                    member.getEmail(), member.getDescription(), member.isEnabled()));
        }
        
        return memberIds;
    }

    public ServiceId registerService(String userToken, String federationId, String ownerId, String endpoint,
            Map<String, String> metadata, String discoveryPolicy, String accessPolicy) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.REGISTER_SERVICE));
        String serviceId = this.federationHost.registerService(requestUser.getId(), federationId, ownerId, endpoint, metadata, discoveryPolicy, accessPolicy);
        return new ServiceId(serviceId);
    }

    public List<ServiceId> getServices(String userToken, String federationId, String ownerId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICES));
        List<String> servicesIds = this.federationHost.getOwnedServices(requestUser.getId(), federationId, ownerId);
        List<ServiceId> services = new ArrayList<ServiceId>();
        
        for (String serviceId : servicesIds) {
            services.add(new ServiceId(serviceId));
        }
        
        return services;
    }
    
    public ServiceInfo getService(String userToken, String federationId, String ownerId, String serviceId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.GET_SERVICE));
        
        FederationService service = this.federationHost.getOwnedService(requestUser.getId(), federationId, ownerId, serviceId);
        return new ServiceInfo(service.getServiceId(), service.getEndpoint(), service.getMetadata(), 
                service.getDiscoveryPolicy().getName(), service.getInvoker().getName());
    }
    
    public List<ServiceDiscovered> discoverServices(String userToken, String federationId, String memberId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.DISCOVER_SERVICES));
        
        List<FederationService> services = this.federationHost.getAuthorizedServices(requestUser.getId(), federationId, memberId);
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
    
    public CloudCredentials map(String userToken, String federationId, String cloudName) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.MAP));
        Map<String, String> credentialsMap = this.federationHost.map(federationId, cloudName);
        return new CloudCredentials(credentialsMap);
    }
    
    private SystemUser authenticate(String userToken) throws FogbowException {
        RSAPublicKey keyRSA = getAsPublicKey();
        return AuthenticationUtil.authenticate(keyRSA, userToken);
    }
    
    private RSAPublicKey getAsPublicKey() throws FogbowException {
        if (this.asPublicKey == null) {
            this.asPublicKey = FhsPublicKeysHolder.getInstance().getAsPublicKey();
        }
        return this.asPublicKey;
    }
    
    public void setAsPublicKey(RSAPublicKey asPublicKey) {
        this.asPublicKey = asPublicKey;
    }

    public String getPublicKey() throws InternalServerErrorException, GeneralSecurityException {
        return CryptoUtil.toBase64(ServiceAsymmetricKeysHolder.getInstance().getPublicKey());
    }
}
