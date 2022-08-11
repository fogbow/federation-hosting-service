package cloud.fogbow.fhs.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationFactory;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationHost {
    public static final String INVOKER_CLASS_NAME_METADATA_KEY = "invokerClassName";
    public static final String CREDENTIALS_METADATA_KEY = "credentials";
    
    private List<FederationUser> federationAdminList;
    private List<Federation> federationList;
    private List<RemoteFederation> remoteFederations;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationFactory federationFactory;
    private DatabaseManager databaseManager;
    private FhsCommunicationMechanism communicationMechanism;
    private SynchronizationMechanism syncMechanism;
    
    public FederationHost(List<FederationUser> federationAdminList, 
            List<Federation> federationList, JsonUtils jsonUtils, 
            FederationAuthenticationPluginInstantiator authenticationPluginInstantiator, 
            FederationFactory federationFactory, DatabaseManager databaseManager, 
            FhsCommunicationMechanism communicationMechanism) {
        this.federationAdminList = federationAdminList;
        this.federationList = federationList;
        this.authenticationPluginInstantiator = authenticationPluginInstantiator;
        this.federationFactory = federationFactory;
        this.databaseManager = databaseManager;
        this.communicationMechanism = communicationMechanism;
    }
    
    public FederationHost(DatabaseManager databaseManager, 
            FhsCommunicationMechanism communicationMechanism) {
        prepareFederationHost(databaseManager, communicationMechanism);
    }
    
    public void reload(DatabaseManager databaseManager, 
            FhsCommunicationMechanism communicationMechanism) {
        prepareFederationHost(databaseManager, communicationMechanism);
    }
    
    private void prepareFederationHost(DatabaseManager databaseManager, 
            FhsCommunicationMechanism communicationMechanism) {
        this.databaseManager = databaseManager;
        this.communicationMechanism = communicationMechanism;
        this.federationAdminList = this.databaseManager.getFederationAdmins();
        this.federationList = this.databaseManager.getFederations();
        this.authenticationPluginInstantiator = new FederationAuthenticationPluginInstantiator();
        this.federationFactory = new FederationFactory();
        // FIXME how reload should affect the remote federations data?
        this.remoteFederations = new ArrayList<RemoteFederation>();
    }

    public void setRemoteFederationsList(List<RemoteFederation> remoteFederations) {
        this.remoteFederations = remoteFederations;
    }
    
    public void setSynchronizationMechanism(SynchronizationMechanism syncMechanism) {
        this.syncMechanism = syncMechanism;
    }
            
    /*
     * 
     * FHSOperator
     * 
     */
    
    public String addFederationAdmin(String adminName, String adminEmail, 
            String adminDescription, boolean enabled, Map<String, String> authenticationProperties) throws InvalidParameterException {
        synchronized(this.federationAdminList) {
            if (adminName == null || adminName.isEmpty()) {
                throw new InvalidParameterException(Messages.Exception.ADMIN_NAME_CANNOT_BE_NULL_OR_EMPTY);
            }
            
            if (lookUpAdminByName(adminName) != null) {
                throw new InvalidParameterException(Messages.Exception.ADMIN_ALREADY_EXISTS);
            }
            
            FederationUser newAdmin = new FederationUser(adminName, "", 
                    PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY), 
                    adminEmail, adminDescription, enabled, authenticationProperties, false, true);
            federationAdminList.add(newAdmin);
            this.databaseManager.saveFederationUser(newAdmin);
            
            return newAdmin.getMemberId();
        }
    }
    
    public FederationUser getFederationAdmin(String adminId) throws InvalidParameterException {
        synchronized(this.federationAdminList) {
            FederationUser admin = lookUpAdminById(adminId);
            
            if (admin == null) {
                throw new InvalidParameterException();
            }
            
            return admin;
        }
    }

    public List<FederationUser> getFederationAdmins() {
        return new ArrayList<FederationUser>(this.federationAdminList);
    }

    public void updateFederationAdmin(String adminId, String name, String email, String description,
            boolean enabled) throws InvalidParameterException {
        FederationUser federationUser = getAdminByIdOrFail(adminId);

        synchronized (federationUser) {
            // This line is used to make sure the user was not deleted while
            // getting the monitor
            getAdminByIdOrFail(adminId);

            federationUser.setName(name);
            federationUser.setEmail(email);
            federationUser.setDescription(description);
            federationUser.setEnabled(enabled);

            this.databaseManager.saveFederationUser(federationUser);
        }
    }
    
    public void deleteFederationAdmin(String adminId) throws InvalidParameterException {
        synchronized(this.federationAdminList) {
            FederationUser federationUser = getAdminByIdOrFail(adminId);
            
            synchronized(federationUser) {
                // This line is used to make sure the user was not deleted while
                // getting the monitor
                getAdminByIdOrFail(adminId);
                federationAdminList.remove(federationUser);
                this.databaseManager.removeFederationUser(federationUser);
            }
        }
    }

    public List<Federation> getFederations() {
        return new ArrayList<Federation>(this.federationList);
    }
    
    public List<Federation> getFederationsInstancesOwnedByAnotherMember(String userId) throws UnauthorizedRequestException {
        synchronized(this.federationList) {
            List<Federation> ownedFederations = new ArrayList<Federation>();
            
            for (Federation federation : this.federationList) {
                if (federation.getOwner().equals(userId)) {
                    ownedFederations.add(federation);
                }
            }
            
            return ownedFederations;
        }
    }

    public void updateFederation(String federationId, boolean enabled) throws InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
            federation.setEnabled(enabled);
            this.databaseManager.saveFederation(federation);
        }
    }
    
    public void deleteFederationInstance(String federationId) throws InvalidParameterException {
        synchronized(this.federationList) {
            Federation federation = getFederationOrFail(federationId);
            
            synchronized(federation) {
                // This line is used to make sure the federation was not deleted while
                // getting the monitor
                getFederationOrFail(federationId);
                this.federationList.remove(federation);
                this.databaseManager.saveFederation(federation);
            }
        }
    }
    
    /*
     * 
     * Federations
     * 
     */
    
    public Federation createFederation(String requester, String federationName, Map<String, String> metadata, 
            String description, boolean enabled) 
            throws UnauthorizedRequestException, InvalidParameterException {
        synchronized(this.federationList) {
            checkIfRequesterIsFedAdmin(requester);
            
            if (federationName == null || federationName.isEmpty()) {
                throw new InvalidParameterException(Messages.Exception.FEDERATION_NAME_CANNOT_BE_NULL_OR_EMPTY);
            }
            
            Federation federation = this.federationFactory.createFederationFactory(requester, federationName, 
                    metadata, description, enabled);
            federationList.add(federation);
            this.databaseManager.saveFederation(federation);
            
            return federation;
        }
    }
    
    public List<Federation> getFederationsOwnedByUser(String requester) throws UnauthorizedRequestException {
        synchronized(this.federationList) {
            checkIfRequesterIsFedAdmin(requester);
            List<Federation> ownedFederations = new ArrayList<Federation>();
            
            for (Federation federation : this.federationList) {
                if (federation.getOwner().equals(requester)) {
                    ownedFederations.add(federation);
                }
            }
            
            return ownedFederations;            
        }
    }

    // TODO test
    public List<Federation> getAdminRemoteFederations(String requester) throws UnauthorizedRequestException {
        synchronized(this.federationList) {
            checkIfRequesterIsFedAdmin(requester);
            List<Federation> remoteFederations = new ArrayList<Federation>();
            
            for (Federation federation : this.federationList) {
                if (federation.isRemoteAdmin(requester)) {
                    remoteFederations.add(federation);
                }
            }
            
            return remoteFederations;
        }
    }

    public Federation getFederation(String requester, String federationId) throws InvalidParameterException, UnauthorizedRequestException {
        synchronized(this.federationList) {
            checkIfRequesterIsFedAdmin(requester);
            Federation federation = getFederationOrFail(federationId);
            return federation;
        }
    }
    
    public void deleteFederation(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        synchronized(this.federationList) {
            checkIfRequesterIsFedAdmin(requester);
            Federation federation = getFederationOrFail(federationId);
            
            synchronized(federation) {
                // This line is used to make sure the federation was not deleted while
                // getting the monitor
                getFederationOrFail(federationId);
                checkIfRequesterIsFederationOwner(requester, federation);
                this.federationList.remove(federation);
                this.databaseManager.removeFederation(federation);
            }
        }
    }

    public List<RemoteFederation> getRemoteFederationList(String requester) throws UnauthorizedRequestException {
        checkIfRequesterIsFedAdmin(requester);
        return new ArrayList<RemoteFederation>(this.remoteFederations);
    }

    public void addUserToAllowedAdmins(String requester, String fedAdminId, String fhsId, String federationId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        federation.addRemoteUserAsAllowedFedAdmin(fedAdminId, fhsId);
    }

    public void removeUserFromAllowedAdmins(String requester, String fedAdminId, String fhsId, String federationId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        checkIfRequesterIsFederationOwner(requester, federation);
        federation.removeRemoteUserFromAllowedAdmins(fedAdminId, fhsId);
    }
    
    public void requestToJoinRemoteFederation(String requester, String federationId) 
            throws FogbowException {
        checkIfRequesterIsFedAdmin(requester);
        RemoteFederation federation = getRemoteFederationOrFail(federationId);
        Federation remoteFederation = this.communicationMechanism.joinRemoteFederation(
                lookUpAdminByName(requester), federation.getFedId(), federation.getOwnerFhsId());
        this.federationList.add(remoteFederation);
    }
    
    // TODO test
    public void updateFederationUsingRemoteData(FederationUpdate remoteUpdate) throws InvalidParameterException {
        Federation federationToUpdate = getFederationOrFail(remoteUpdate.getTargetFederationId());
        
        synchronized(federationToUpdate) {
            federationToUpdate.update(remoteUpdate);
        }
    }
    
    /*
     * 
     * Membership
     * 
     */
    
    public FederationUser grantMembership(String requester, String federationId, String userId, String email, String description, 
            Map<String, String> authenticationProperties) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federationToAdd = getFederationOrFail(federationId);
        
        synchronized(federationToAdd) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federationToAdd);
            FederationUser newMember = federationToAdd.addUser(userId, email, description, authenticationProperties);
            this.databaseManager.saveFederation(federationToAdd);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withMembers(Arrays.asList(newMember)).
                    build();
            this.syncMechanism.onLocalUpdate(update);
            
            return newMember;
        }
    }
    
    public List<FederationUser> getFederationMembers(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation)  {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            return federation.getMemberList();
        }
    }
    
    public FederationUser getFederationMemberInfo(String requester, String federationId, String memberId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            return federation.getUserByMemberId(memberId);
        }
    }
    
    public void revokeMembership(String requester, String federationId, String memberId) throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            federation.revokeMembership(memberId);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    deleteMember(memberId).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    /*
     * 
     * Attributes
     * 
     */
    
    public String createAttribute(String requester, String federationId, String attributeName)
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            String newAttributeId = federation.createAttribute(attributeName);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withAttributes(Arrays.asList(federation.getAttribute(newAttributeId))).
                    build();
            this.syncMechanism.onLocalUpdate(update);
            
            return newAttributeId;
        }
    }

    public List<FederationAttribute> getFederationAttributes(String requester, String federationId)
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            return federation.getAttributes();
        }
    }
    
    public void deleteAttribute(String requester, String federationId, String attributeId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            federation.deleteAttribute(attributeId);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    deleteAttribute(attributeId).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    public void grantAttribute(String requester, String federationId, String memberId, String attributeId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            federation.grantAttribute(memberId, attributeId);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withMembers(Arrays.asList(federation.getUserByMemberId(memberId))).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    public void revokeAttribute(String requester, String federationId, String memberId, String attributeId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        checkIfRequesterIsFedAdmin(requester);
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            checkIfRequesterIsFederationOwner(requester, federation);
            federation.revokeAttribute(memberId, attributeId);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withMembers(Arrays.asList(federation.getUserByMemberId(memberId))).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    /*
     * 
     * Services
     * 
     */
    
    public String registerService(String requester, String federationId, String endpoint, Map<String, String> metadata, 
            String discoveryPolicyClassName, String accessPolicyClassName) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
            if (!federation.isServiceOwner(requester)) {
                throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
            }

            String serviceId = federation.registerService(requester, endpoint, discoveryPolicyClassName, 
                    accessPolicyClassName, metadata);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withServices(Arrays.asList(federation.getService(serviceId).serialize())).
                    build();
            this.syncMechanism.onLocalUpdate(update);
            
            return serviceId;
        }
    }

    public List<String> getOwnedServices(String requester, String federationId) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
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
    }
    
    public FederationService getOwnedService(String requester, String federationId, String serviceId) throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
            if (!federation.isServiceOwner(requester)) {
                throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
            }
            
            for (FederationService service : federation.getServices()) {
                if (service.getOwnerId().equals(requester) &&
                        service.getServiceId().equals(serviceId)) {
                    return service;
                }
            }
            
            throw new InvalidParameterException(
                    String.format(Messages.Exception.SERVICE_NOT_FOUND, serviceId, federationId));            
        }
    }

    public void updateService(String requester, String federationId, String ownerId, String serviceId,
            Map<String, String> metadata, String discoveryPolicyClassName, String accessPolicyClassName) 
                    throws InvalidParameterException, UnauthorizedRequestException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
            if (!federation.isServiceOwner(requester)) {
                throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
            }
            
            FederationService federationService = federation.getService(serviceId);
            federationService.update(metadata, discoveryPolicyClassName, accessPolicyClassName);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    withServices(Arrays.asList(federation.getService(serviceId).serialize())).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    public void deleteService(String requester, String federationId, String owner, String serviceId) 
            throws UnauthorizedRequestException, InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        
        synchronized(federation) {
            // This line is used to make sure the federation was not deleted while
            // getting the monitor
            getFederationOrFail(federationId);
            
            if (!federation.isServiceOwner(requester)) {
                throw new UnauthorizedRequestException(Messages.Exception.REQUESTER_IS_NOT_SERVICE_OWNER);
            }
            
            federation.deleteService(serviceId);
            this.databaseManager.saveFederation(federation);
            
            FederationUpdate update = new FederationUpdate.FederationUpdateBuilder().
                    updateFederation(federationId).
                    deleteService(serviceId).
                    build();
            this.syncMechanism.onLocalUpdate(update);
        }
    }
    
    public List<FederationService> getAuthorizedServices(String requester, String federationId) throws InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        return federation.getAuthorizedServices(requester);
    }
    
    public ServiceResponse invokeService(String requester, String federationId, String serviceId, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        Federation federation = lookUpFederationById(federationId);
        return federation.invoke(requester, federationId, serviceId, method, path, headers, body);
    }
    
    /*
     * 
     * Authentication
     * 
     */

    public String login(String federationId, String memberId, Map<String, String> credentials) throws InvalidParameterException, 
    UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        Federation federation = getFederationOrFail(federationId);
        return federation.login(memberId, credentials);
    }

    public String federationAdminLogin(String federationAdminId, Map<String, String> credentials) throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        FederationUser admin = getAdminByIdOrFail(federationAdminId);
        String identityPluginClassName = admin.getIdentityPluginClassName();
        Map<String, String> identityPluginProperties = admin.getIdentityPluginProperties();
        FederationAuthenticationPlugin authenticationPlugin = 
                this.authenticationPluginInstantiator.getAuthenticationPlugin(identityPluginClassName, identityPluginProperties);
        return authenticationPlugin.authenticate(credentials);
    }
    
    /*
     * 
     * Authorization
     * 
     */
    
    public Map<String, String> map(String federationId, String serviceId, String userId, String cloudName) throws InvalidParameterException {
        Federation federation = lookUpFederationById(federationId);
        return federation.map(serviceId, userId, cloudName);
    }
    
    /*
     * 
     * Remote federations (methods called by RemoteFacade)
     * 
     */
    
    public void updateRemoteFederationList(String fhsId, List<RemoteFederation> fhsRemoteFederations) {
        this.remoteFederations.removeAll(fhsRemoteFederations);
        this.remoteFederations.addAll(fhsRemoteFederations);
    }
    
    public Federation joinRemoteFederation(FederationUser requester, String fhsId, String federationId) throws InvalidParameterException {
        Federation federation = getFederationOrFail(federationId);
        federation.addRemoteAdmin(requester, fhsId);
        return federation;
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
    
    private FederationUser getAdminByIdOrFail(String adminId) throws InvalidParameterException {
        FederationUser federationUser = lookUpAdminById(adminId);
        
        if (federationUser == null) { 
            throw new InvalidParameterException(
                    String.format(Messages.Exception.CANNOT_FIND_FEDERATION_ADMIN, adminId));
        }
        
        return federationUser;
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
    
    private void checkIfRequesterIsFederationOwner(String requester, Federation federation) 
            throws InvalidParameterException, UnauthorizedRequestException {
        if (!federation.isFederationOwner(requester)) {
            throw new UnauthorizedRequestException(
                    String.format(Messages.Exception.REQUESTER_DOES_NOT_OWN_FEDERATION, requester, federation.getId()));
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
    
    private RemoteFederation getRemoteFederationOrFail(String federationId) throws InvalidParameterException {
        for (RemoteFederation federation : this.remoteFederations) {
            if (federation.getFedId().equals(federationId)) {
                return federation;
            }
        }
        
        throw new InvalidParameterException(
                    String.format(Messages.Exception.CANNOT_FIND_FEDERATION, federationId));
    }
}
