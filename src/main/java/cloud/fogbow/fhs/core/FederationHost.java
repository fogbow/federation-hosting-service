package cloud.fogbow.fhs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.JoinRequest;
import cloud.fogbow.fhs.core.models.ServiceOperation;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationHost {
    public static final String INVOKER_CLASS_NAME_METADATA_KEY = "invokerClassName";
    public static final String CREDENTIALS_METADATA_KEY = "credentials";
    
    private List<FederationUser> federationAdminList;
    private List<Federation> federationList;
    private ServiceInvokerInstantiator serviceInvokerInstantiator;
    private DiscoveryPolicyInstantiator discoveryPolicyInstantiator;
    private AccessPolicyInstantiator accessPolicyInstantiator;
    private JsonUtils jsonUtils;
    
    public FederationHost(List<FederationUser> federationAdminList, 
            List<Federation> federationList, ServiceInvokerInstantiator serviceInvokerInstantiator,
            DiscoveryPolicyInstantiator discoveryPolicyInstantiator, 
            AccessPolicyInstantiator accessPolicyInstantiator, JsonUtils jsonUtils) {
        this.federationAdminList = federationAdminList;
        this.federationList = federationList;
        this.serviceInvokerInstantiator = serviceInvokerInstantiator;
        this.discoveryPolicyInstantiator = discoveryPolicyInstantiator;
        this.accessPolicyInstantiator = accessPolicyInstantiator;
        this.jsonUtils = jsonUtils;
    }
    
    public FederationHost() {
        this.federationAdminList = new ArrayList<FederationUser>();
        this.federationList = new ArrayList<Federation>();
        this.serviceInvokerInstantiator = new ServiceInvokerInstantiator();
        this.discoveryPolicyInstantiator = new DiscoveryPolicyInstantiator();
        this.accessPolicyInstantiator = new AccessPolicyInstantiator();
        this.jsonUtils = new JsonUtils();
    }
    
    /*
     * 
     * FHSOperator
     * 
     */
    
    public String addFederationAdmin(String adminName, String adminEmail, 
            String adminDescription, boolean enabled) throws InvalidParameterException {
        if (adminName == null || adminName.isEmpty()) {
            throw new InvalidParameterException(Messages.Exception.ADMIN_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        
        if (lookUpAdminByName(adminName) != null) {
            throw new InvalidParameterException(Messages.Exception.ADMIN_ALREADY_EXISTS);
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
    
    /*
     * 
     * Federations
     * 
     */
    
    public Federation createFederation(String requester, String federationName, Map<String, String> metadata, 
            String description, boolean enabled) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        
        if (federationName == null || federationName.isEmpty()) {
            throw new InvalidParameterException(Messages.Exception.FEDERATION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        
        Federation federation = new Federation(requester, federationName, metadata, description, enabled);
        federationList.add(federation);
        return federation;
    }
    
    public List<Federation> getFederationsOwnedByUser(String requester) throws UnauthorizedRequestException {
        checkIfRequesterIsFedAdmin(requester);
        List<Federation> ownedFederations = new ArrayList<Federation>();
        
        for (Federation federation : this.federationList) {
            if (federation.getOwner().equals(requester)) {
                ownedFederations.add(federation);
            }
        }
        
        return ownedFederations;
    }

    // TODO test
    public Federation getFederation(String requester, String federationId) throws InvalidParameterException, UnauthorizedRequestException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId); 
        return federation;
    }
    
    public void updateFederation(Federation federationToUpdate) {
        // TODO implement
    }
    
    public void deleteFederation(String federationId) {
        // TODO implement
    }
    
    /*
     * 
     * Membership
     * 
     */
    
    public FederationUser grantMembership(String requester, String federationId, String userId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federationToAdd = getFederationOrFail(federationId); 
        checkIfRequesterIsFederationOwner(requester, federationToAdd);
        return federationToAdd.addUser(userId);
    }
    
    public List<FederationUser> getFederationMembers(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        return federation.getMemberList();
    }
    
    public FederationUser getFederationMemberInfo(String requester, String federationId, String memberId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        return federation.getUserByMemberId(memberId);
    }
    
    public void revokeMembership(String federationId, String memberId) {
        // TODO implement
    }
    
    /*
     * 
     * Attributes
     * 
     */
    
    public String createAttribute(String requester, String federationId, String attributeName)
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        return federation.createAttribute(attributeName);
    }

    public List<FederationAttribute> getFederationAttributes(String requester, String federationId)
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        return federation.getAttributes();
    }
    
    public void deleteAttribute(String federationId, String attributeId) {
        // TODO implement
    }
    
    public void grantAttribute(String requester, String federationId, String memberId, String attributeId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        federation.grantAttribute(memberId, attributeId);
    }
    
    public void revokeAttribute(String requester, String federationId, String memberId, String attributeId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = lookUpFederationById(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        federation.revokeAttribute(memberId, attributeId);
    }
    
    /*
     * 
     * Services
     * 
     */
    
    public String registerService(String requester, String federationId, String endpoint, Map<String, String> metadata, 
            String discoveryPolicyClassName, String accessPolicyClassName) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        if (!federation.isServiceOwner(requester)) {
            throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
        }

        if (endpoint == null || endpoint.isEmpty()) {
            throw new InvalidParameterException(
                    Messages.Exception.SERVICE_ENDPOINT_CANNOT_BE_NULL_OR_EMPTY);
        }
        
        ServiceDiscoveryPolicy discoveryPolicy = this.discoveryPolicyInstantiator.getDiscoveryPolicy(discoveryPolicyClassName);
        ServiceAccessPolicy accessPolicy = this.accessPolicyInstantiator.getAccessPolicy(accessPolicyClassName, metadata);
        
        String invokerClassName = metadata.get(INVOKER_CLASS_NAME_METADATA_KEY);
        ServiceInvoker invoker = this.serviceInvokerInstantiator.getInvoker(invokerClassName, metadata, federationId);
        
        FederationService service = new FederationService(requester, endpoint, discoveryPolicy, accessPolicy, invoker, metadata);
        federation.registerService(service);
        
        return service.getServiceId();
    }

    public List<String> getOwnedServices(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        if (!federation.isServiceOwner(requester)) {
            throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
        }
        
        List<String> ownedServicesIds = new ArrayList<String>();

        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(requester)) {
                ownedServicesIds.add(service.getServiceId());
            }
        }
            
        return ownedServicesIds;
    }
    
    public FederationService getOwnedService(String requester, String federationId, String serviceId) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        if (!federation.isServiceOwner(requester)) {
            throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
        }
        
        for (FederationService service : federation.getServices()) {
            if (service.getOwnerId().equals(requester) &&
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
    
    public List<FederationService> getAuthorizedServices(String requester, String federationId) throws InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        return federation.getAuthorizedServices(requester);
    }
    
    public ServiceResponse invokeService(String requester, String federationId, String serviceId, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        Federation federation = lookUpFederationById(federationId);
        FederationService service = federation.getService(serviceId);
        ServiceAccessPolicy accessPolicy = service.getAccessPolicy();
        FederationUser federationUser = federation.getUserById(requester);
        ServiceOperation operation = new ServiceOperation(method);
        
        if (accessPolicy.isAllowedToPerform(federationUser, operation)) {
            return service.invoke(federationUser, method, path, headers, body);
        }
        
        throw new UnauthorizedRequestException(Messages.Exception.USER_IS_NOT_AUTHORIZED_TO_PERFORM_OPERATION_ON_SERVICE);
    }
    
    /*
     * 
     * Authorization
     * 
     */
    
    public Map<String, String> map(String federationId, String cloudName) {
        Federation federation = lookUpFederationById(federationId);
        Map<String, String> metadata = federation.getMetadata();
        Map<String, Map<String, String>> credentials = 
                this.jsonUtils.fromJson(metadata.get(CREDENTIALS_METADATA_KEY), Map.class);
        return credentials.get(cloudName);
    }
    
    /*
     * 
     * Remote federations
     * 
     */
    
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
    
    private void checkIfRequesterIsFedAdmin(String requester) throws UnauthorizedRequestException {
        if (lookUpAdminByName(requester) == null) {
            throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_ADMIN);
        }
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
    
    private void checkIfRequesterIsFederationOwner(String requester, Federation federation) throws InvalidParameterException, UnauthorizedRequestException {
        // FIXME should check for other fed admins 
        // in the federation
        if (!federation.getOwner().equals(requester)) {
            // TODO add message
            throw new UnauthorizedRequestException();
        }
    }
    
    private Federation getFederationOrFail(String federationId) throws InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        
        if (federation == null) {
            throw new InvalidParameterException(
                    String.format(Messages.Exception.CANNOT_FIND_FEDERATION, federationId));
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
}
