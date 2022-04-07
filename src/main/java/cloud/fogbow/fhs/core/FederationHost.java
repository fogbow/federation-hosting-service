package cloud.fogbow.fhs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.JoinRequest;
import cloud.fogbow.fhs.core.models.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.models.ServiceInvoker;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.discovery.AllowAllServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.models.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.models.invocation.DefaultServiceInvoker;
import cloud.fogbow.fhs.core.models.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationHost {
    public static final String CREDENTIALS_METADATA_KEY = "credentials";
    
    private List<FederationUser> federationAdminList;
    private List<Federation> federationList;
    private ServiceInvokerInstantiator serviceInvokerInstantiator;
    private DiscoveryPolicyInstantiator discoveryPolicyInstantiator;
    private JsonUtils jsonUtils;
    
    public FederationHost(List<FederationUser> federationAdminList, 
            List<Federation> federationList, ServiceInvokerInstantiator serviceInvokerInstantiator,
            DiscoveryPolicyInstantiator discoveryPolicyInstantiator, JsonUtils jsonUtils) {
        this.federationAdminList = federationAdminList;
        this.federationList = federationList;
        this.serviceInvokerInstantiator = serviceInvokerInstantiator;
        this.discoveryPolicyInstantiator = discoveryPolicyInstantiator;
        this.jsonUtils = jsonUtils;
    }
    
    public FederationHost() {
        this.federationAdminList = new ArrayList<FederationUser>();
        this.federationList = new ArrayList<Federation>();
        this.serviceInvokerInstantiator = new ServiceInvokerInstantiator();
        this.discoveryPolicyInstantiator = new DiscoveryPolicyInstantiator();
        this.jsonUtils = new JsonUtils();
    }
    
    public String addFederationAdmin(String adminName, String adminEmail, 
            String adminDescription, boolean enabled) throws InvalidParameterException {
        if (adminName == null || adminName.isEmpty()) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        if (lookUpAdminByName(adminName) != null) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        FederationUser newAdmin = new FederationUser(adminName, adminEmail, adminDescription, enabled);
        federationAdminList.add(newAdmin);
        return newAdmin.getMemberId();
    }
    
    public FederationUser getFederationAdmin(String adminId) throws InvalidParameterException {
        FederationUser admin = lookUpAdminById(adminId);
            
        if (admin == null) {
            throw new InvalidParameterException();
        }
        
        return admin;
    }
    
    private FederationUser lookUpAdminById(String adminId) {
        for (FederationUser admin : federationAdminList) {
            if (admin.getMemberId().equals(adminId)) {
                return admin;
            }
        }
        
        return null;
    }
    
    private FederationUser lookUpAdminByName(String adminName) {
        for (FederationUser admin : federationAdminList) {
            if (admin.getName().equals(adminName)) {
                return admin;
            }
        }
        
        return null;
    }
    
    public Federation createFederation(String requester, String federationName, Map<String, String> metadata, 
            String description, boolean enabled) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        
        if (federationName == null || federationName.isEmpty()) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        Federation federation = new Federation(requester, federationName, metadata, description, enabled);
        federationList.add(federation);
        return federation;
    }

    private void checkIfRequesterIsFedAdmin(String requester) throws UnauthorizedRequestException {
        if (lookUpAdminByName(requester) == null) {
            // TODO add message
            throw new UnauthorizedRequestException();
        }
    }

    // TODO test
    public Federation getFederation(String requester, String federationId) throws InvalidParameterException, UnauthorizedRequestException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId); 
        return federation;
    }
    
    private Federation getFederationOrFail(String federationId) throws InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        
        if (federation == null) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        return federation;
    }
    
    private Federation lookUpFederationById(String federationId) {
        for (Federation federation : this.federationList) {
            if (federation.getId().equals(federationId)) {
                return federation;
            }
        }
        
        return null;
    }
    
    public void updateFederation(Federation federationToUpdate) {
        // TODO implement
    }
    
    public void deleteFederation(String federationId) {
        // TODO implement
    }
    
    public List<FederationAttribute> getFederationAttributes(String federationId) {
        // TODO implement
        return null;
    }
    
    public String createAttribute(String federationId, Map<String, String> attributeData) {
        // TODO implement
        return null;
    }
    
    public void deleteAttribute(String federationId, String attributeId) {
        // TODO implement
    }
    
    public void grantAttribute(String federationId, String memberId, String attributeId) {
        // TODO implement
    }
    
    public void revokeAttribute(String federationId, String memberId, String attributeId) {
        // TODO implement
    }
    
    public List<FederationService> getAuthorizedServices(String requester, String federationId, String memberId) {
        Federation federation = lookUpFederationById(federationId);
        return federation.getAuthorizedServices(memberId);
    }
    
    public List<Federation> getFederationsOwnedByUser(String requester, String owner) throws UnauthorizedRequestException {
        checkIfRequesterIsFedAdmin(requester);
        List<Federation> ownedFederations = new ArrayList<Federation>();
        
        for (Federation federation : this.federationList) {
            if (federation.getOwner().equals(owner)) {
                ownedFederations.add(federation);
            }
        }
        
        return ownedFederations;
    }
    
    public void requestToJoinFederation(String federationId, String fhsUrl, String memberId) {
        // TODO implement
    }
    
    public List<JoinRequest> getJoinRequests(String memberId) {
        // TODO implement
        return null;
    }
    
    public void grantJoinRequest(String memberId, String requestId) {
        // TODO implement
    }
    
    public void denyJoinRequest(String memberId, String requestId) {
        // TODO implement
    }
    
    public void leaveFederation(String federationId, String memberId) {
        // TODO implement
    }
    
    public ServiceResponse invokeService(String requester, String federationId, String serviceId, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        // TODO should check if user is authorized
        Federation federation = lookUpFederationById(federationId);
        FederationService service = federation.getService(serviceId);
        return service.invoke(federation.getUser(requester), method, path, headers, body);
    }
    
    public List<FederationUser> getFederationMembers(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsOwner(requester, federation);
        return federation.getMemberList();
    }
   
    private void checkIfRequesterIsOwner(String requester, Federation federation) throws InvalidParameterException, UnauthorizedRequestException {
        // FIXME should check for other fed admins 
        // in the federation
        if (!federation.getOwner().equals(requester)) {
            // TODO add message
            throw new UnauthorizedRequestException();
        }
    }
    
    public FederationUser grantMembership(String requester, String federationId, String userId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federationToAdd = getFederationOrFail(federationId); 
        checkIfRequesterIsOwner(requester, federationToAdd);
        return federationToAdd.addUser(userId);
    }

    public FederationUser getFederationMemberInfo(String requester, String federationId, String memberId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        checkIfRequesterIsOwner(requester, federation);
        return federation.getUser(memberId);
    }
    
    public void revokeMembership(String federationId, String memberId) {
        // TODO implement
    }
    
    public String registerService(String requester, String federationId, String owner, String endpoint, Map<String, String> metadata, 
            String discoveryPolicyClassName, String invokerClassName) throws UnauthorizedRequestException, InvalidParameterException {
        // FIXME it should check if the requester is service owner
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        checkIfRequesterIsOwner(requester, federation);
        
        if (owner == null || owner.isEmpty()) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        if (endpoint == null || endpoint.isEmpty()) {
            // TODO add message
            throw new InvalidParameterException();
        }
        
        ServiceDiscoveryPolicy discoveryPolicy = getDiscoveryPolicy(discoveryPolicyClassName);
        ServiceInvoker invoker = getInvoker(invokerClassName, metadata, federationId);
        FederationService service = new FederationService(owner, endpoint, discoveryPolicy, invoker, metadata);
        
        federation.registerService(service);
        
        return service.getServiceId();
    }

    private ServiceInvoker getInvoker(String invokerClassName, Map<String, String> metadata, String federationId) {
        if (invokerClassName == null || invokerClassName.isEmpty()) {
            return new DefaultServiceInvoker();
        } else {
            return this.serviceInvokerInstantiator.getInvoker(invokerClassName, metadata, federationId);
        }
    }

    private ServiceDiscoveryPolicy getDiscoveryPolicy(String discoveryPolicyClassName) {
        if (discoveryPolicyClassName == null || discoveryPolicyClassName.isEmpty()) {
            return new AllowAllServiceDiscoveryPolicy();
        } else {
            return this.discoveryPolicyInstantiator.getDiscoveryPolicy(discoveryPolicyClassName);
        }
    }

    public List<String> getOwnedServices(String requester, String federationId, String ownerId) throws UnauthorizedRequestException, InvalidParameterException {
        // FIXME it should check if the requester is service owner
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        List<String> ownedServicesIds = new ArrayList<String>();

        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(ownerId)) {
                ownedServicesIds.add(service.getServiceId());
            }
        }
            
        return ownedServicesIds;
    }
    
    public FederationService getOwnedService(String requester, String federationId, String ownerId, String serviceId) throws UnauthorizedRequestException, InvalidParameterException {
        // FIXME it should check if the requester is service owner
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        
        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(ownerId) &&
                    service.getServiceId().equals(serviceId)) {
                return service;
            }
        }

        // TODO add message
        // TODO test
        throw new InvalidParameterException();
    }
    
    public void updateService(String federationId, String owner, String serviceId, 
            FederationService updatedService) {
        // TODO implement
    }
    
    public void deleteService(String federationId, String owner, String serviceId) {
        // TODO implement        
    }
    
    public List<Federation> getRemoteFederations() {
        // TODO implement
        return null;
    }
    
    public void joinRemoteFederation(String federationId) {
        // TODO implement
    }
    
    public void leaveRemoteFederation(String federationId) {
        // TODO implement
    }
    
    public void updateRemoteFederation(Federation federation) {
        // TODO implement
    }

    public Map<String, String> map(String federationId, String cloudName) {
        Federation federation = lookUpFederationById(federationId);
        Map<String, String> metadata = federation.getMetadata();
        Map<String, Map<String, String>> credentials = 
                this.jsonUtils.fromJson(metadata.get(CREDENTIALS_METADATA_KEY), Map.class);
        return credentials.get(cloudName);
    }
}
