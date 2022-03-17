package cloud.fogbow.fhs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.JoinRequest;

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
    
    public List<FederationService> getAuthorizedServices(String federationId, String memberId) {
        return null;
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
    
    public void invokeService(String federationId, String serviceId, HttpMethod method, Map<String, String> requestData) {
        
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
