package cloud.fogbow.fhs.core;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;

public class ApplicationFacade {

    private static ApplicationFacade instance;
    private RSAPublicKey asPublicKey;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private LocalFederationHost localFederationHost;
    
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
    
    public void setLocalFederationHost(LocalFederationHost localFederationHost) {
        this.localFederationHost = localFederationHost;
    }
    
    public String addFederationAdmin(String userToken, String adminName, String adminEmail, 
            String adminDescription, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.ADD_FED_ADMIN));
        return this.localFederationHost.addFederationAdmin(adminName, adminEmail, adminDescription, enabled);
    }
    
    public FederationId createFederation(String userToken, String name, String description, boolean enabled) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.CREATE_FEDERATION));
        Federation createdFederation = this.localFederationHost.createFederation(requestUser.getId(), name, description, enabled);
        return new FederationId(createdFederation.getName(), createdFederation.getId(), createdFederation.enabled());
    }
    
    public List<FederationDescription> listFederations(String userToken, String owner) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_FEDERATIONS));
        List<Federation> federationList = this.localFederationHost.getFederationsOwnedByUser(requestUser.getId(), owner);
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
        FederationUser user = this.localFederationHost.grantMembership(requestUser.getId(), federationId, userId);
        return new MemberId(user.getMemberId());
    }
    
    public List<FederationMember> listMembers(String userToken, String federationId) throws FogbowException {
        SystemUser requestUser = authenticate(userToken);
        this.authorizationPlugin.isAuthorized(requestUser, new FhsOperation(OperationType.LIST_MEMBERS));
        List<FederationUser> members = this.localFederationHost.getFederationMembers(requestUser.getId(), federationId);
        List<FederationMember> memberIds = new ArrayList<FederationMember>();
        
        for (FederationUser member : members) {
            memberIds.add(new FederationMember(member.getMemberId(), member.getName(), 
                    member.getEmail(), member.getDescription(), member.isEnabled()));
        }
        
        return memberIds;
    }
    
    protected SystemUser authenticate(String userToken) throws FogbowException {
        RSAPublicKey keyRSA = getAsPublicKey();
        return AuthenticationUtil.authenticate(keyRSA, userToken);
    }
    
    protected RSAPublicKey getAsPublicKey() throws FogbowException {
        if (this.asPublicKey == null) {
            this.asPublicKey = FhsPublicKeysHolder.getInstance().getAsPublicKey();
        }
        return this.asPublicKey;
    }
}
