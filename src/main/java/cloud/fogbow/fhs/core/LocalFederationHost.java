package cloud.fogbow.fhs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.JoinRequest;
import cloud.fogbow.fhs.core.models.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.models.ServiceInvoker;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.discovery.AllowAllServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.models.invocation.DefaultServiceInvoker;

public class LocalFederationHost {

    private List<FederationUser> federationAdminList;
    private List<Federation> federationList;
    
    public LocalFederationHost() {
        this.federationAdminList = new ArrayList<FederationUser>();
        this.federationList = new ArrayList<Federation>();
    }
    
    public String addFederationAdmin(String adminName, String adminEmail, 
            String adminDescription, boolean enabled) {
        // TODO validation
        FederationUser newAdmin = new FederationUser(adminName, adminEmail, adminDescription, enabled);
        federationAdminList.add(newAdmin);
        return newAdmin.getMemberId();
    }
    
    public Federation createFederation(String requester, String federationName, String description, boolean enabled) {
        checkIfRequesterIsFedAdmin(requester);
        
        Federation federation = new Federation(requester, federationName, enabled);
        federationList.add(federation);
        return federation;
    }
    
    private void checkIfRequesterIsFedAdmin(String requester) {
        // TODO Auto-generated method stub
        
    }

    public Federation getFederation(String federationId) {
        return null;
    }
    
    public void updateFederation(Federation federationToUpdate) {
        
    }
    
    public void deleteFederation(String federationId) {
        
    }
    
    public List<FederationAttribute> getFederationAttributes(String federationId) {
        return null;
    }
    
    public String createAttribute(String federationId, Map<String, String> attributeData) {
        return null;
    }
    
    public void deleteAttribute(String federationId, String attributeId) {
        
    }
    
    public void grantAttribute(String federationId, String memberId, String attributeId) {
        
    }
    
    public void revokeAttribute(String federationId, String memberId, String attributeId) {
        
    }
    
    public List<FederationService> getAuthorizedServices(String requester, String federationId, String memberId) {
        Federation federation = lookUpFederationById(federationId);
        List<FederationService> authorizedServices = new ArrayList<FederationService>();
        
        // TODO move this code to federation
        for (FederationService service : federation.getServices()) {
            if (service.getDiscoveryPolicy().isDiscoverableBy(federation.getUser(memberId))) {
                authorizedServices.add(service);
            }
        }
        
        return authorizedServices;
    }
    
    public List<Federation> getFederationsOwnedByUser(String requester, String owner) {
        return this.federationList;
    }
    
    public void requestToJoinFederation(String federationId, String fhsUrl, String memberId) {
        
    }
    
    public List<JoinRequest> getJoinRequests(String memberId) {
        return null;
    }
    
    public void grantJoinRequest(String memberId, String requestId) {
        
    }
    
    public void denyJoinRequest(String memberId, String requestId) {
        
    }
    
    public void leaveFederation(String federationId, String memberId) {
        
    }
    
    public ServiceResponse invokeService(String requester, String federationId, String serviceId, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, String> body) throws FogbowException {
        Federation federation = lookUpFederationById(federationId);
        FederationService service = federation.getService(serviceId);
        return service.invoke(federation.getUser(requester), method, path, headers, body);
    }
    
    public List<FederationUser> getFederationMembers(String requester, String federationId) {
        checkIfRequesterIsFedAdmin(requester);
        
        Federation federation = lookUpFederationById(federationId);
        return federation.getMemberList();
    }
   
    public FederationUser grantMembership(String requester, String federationId, String userId) {
        checkIfRequesterIsFedAdmin(requester);
        // TODO check if requester is owner
        
        Federation federationToAdd = lookUpFederationById(federationId);
        return federationToAdd.addUser(userId);
    }
    
    private Federation lookUpFederationById(String federationId) {
        for (Federation federation : this.federationList) {
            if (federation.getId().equals(federationId)) {
                return federation;
            }
        }
        // FIXME should throw exception
        return null;
    }

    public FederationUser getFederationMemberInfo(String requester, String federationId, String memberId) {
        checkIfRequesterIsFedAdmin(requester);
        
        Federation federation = lookUpFederationById(federationId);
        return federation.getUser(memberId);
    }
    
    public void revokeMembership(String federationId, String memberId) {
        
    }
    
    public String registerService(String requester, String federationId, String owner, String endpoint, Map<String, String> metadata, 
            String discoveryPolicyName, String accessPolicy) {
        // TODO validation
        ServiceDiscoveryPolicy discoveryPolicy = getDiscoveryPolicy(discoveryPolicyName);
        ServiceInvoker invoker = getInvoker(accessPolicy);
        FederationService service = new FederationService(owner, endpoint, discoveryPolicy, invoker, metadata);
        
        Federation federation = lookUpFederationById(federationId);
        federation.registerService(service);
        
        return service.getServiceId();
    }
    
    private ServiceInvoker getInvoker(String accessPolicy) {
        if (accessPolicy.equals("")) {
            return new DefaultServiceInvoker();
        }
        return null;
    }

    private ServiceDiscoveryPolicy getDiscoveryPolicy(String discoveryPolicyName) {
        if (discoveryPolicyName.equals("allowall")) {
            return new AllowAllServiceDiscoveryPolicy();
        }
        return null;
    }

    public List<String> getOwnedServices(String requester, String federationId, String ownerId) {
        Federation federation = lookUpFederationById(federationId);
        List<String> ownedServicesIds = new ArrayList<String>();
        
        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(ownerId)) {
                ownedServicesIds.add(service.getServiceId());
            }
        }
            
        return ownedServicesIds;
    }
    
    public FederationService getOwnedService(String federationId, String ownerId, String serviceId) {
        Federation federation = lookUpFederationById(federationId);
        
        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(ownerId) &&
                    service.getServiceId().equals(serviceId)) {
                return service;
            }
        }

        // FIXME should throw exception
        return null;
    }
    
    public void updateService(String federationId, String owner, String serviceId, 
            FederationService updatedService) {
        
    }
    
    public void deleteService(String federationId, String owner, String serviceId) {
        
    }
}
