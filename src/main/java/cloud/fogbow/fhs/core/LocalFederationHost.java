package cloud.fogbow.fhs.core;

import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.JoinRequest;

public class LocalFederationHost {

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
    
    public List<FederationService> getAuthorizedServices(String federationId, String memberId) {
        return null;
    }
    
    public List<Federation> getFederationsOwnedByUser(String memberId) {
        return null;
    }
    
    public Federation createFederation(String federationName, String federationOwner) {
        return null;
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
    
    public void invokeService(String federationId, String serviceId, HttpMethod method, Map<String, String> requestData) {
        
    }
    
    public List<FederationUser> getFederationMembers(String federationId) {
        return null;
    }
   
    public void grantMembership(String federationId, String memberId) {
        
    }
    
    public FederationUser getFederationMemberInfo(String federationId, String memberId) {
        return null;
    }
    
    public void revokeMembership(String federationId, String memberId) {
        
    }
    
    public String registerService(FederationService service) {
        return null;
    }
    
    public List<String> getOwnedServices(String federationId, String owner) {
        return null;
    }
    
    public List<String> getOwnedService(String federationId, String owner, String serviceId) {
        return null;
    }
    
    public void updateService(String federationId, String owner, String serviceId, 
            FederationService updatedService) {
        
    }
    
    public void deleteService(String federationId, String owner, String serviceId) {
        
    }
}
