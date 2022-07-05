package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;

public class RemoteFacade {
    private static RemoteFacade instance;
    
    private List<String> allowedFhsIds;
    private FederationHost federationHost;
    
    public static RemoteFacade getInstance() {
        synchronized (RemoteFacade.class) {
            if (instance == null) {
                instance = new RemoteFacade();
            }
            return instance;
        }
    }
    
    private RemoteFacade() {

    }
    
    public static List<String> loadAllowedFhsIdsOrFail() throws ConfigurationErrorException {
        String allowedFhsIdsListString = 
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY);
        
        if (allowedFhsIdsListString == null) { 
            throw new ConfigurationErrorException(Messages.Exception.MISSING_ALLOWED_FHS_IDS_PROPERTY);
        }
        
        List<String> allowedFhsIds = new ArrayList<String>();
        
        if (!allowedFhsIdsListString.isEmpty()) {
            for (String allowedFhsId :  allowedFhsIdsListString.split(SystemConstants.ALLOWED_FHS_IDS_SEPARATOR)) {
                allowedFhsIds.add(allowedFhsId);
            }
        }
        
        return allowedFhsIds;
    }
    
    public void setFederationHost(FederationHost federationHost) {
        this.federationHost = federationHost;
    }
    
    public void setAllowedFhsIds(List<String> allowedFhsIds) {
        this.allowedFhsIds = allowedFhsIds;
    }
    
    public List<FederationInstance> getFederationList(String requesterFhsId) throws FogbowException {
        if (this.allowedFhsIds.contains(requesterFhsId)) {
            List<Federation> federations = this.federationHost.getFederations();
            List<FederationInstance> federationInstances = new ArrayList<FederationInstance>();
            
            for (Federation federation : federations) {
                federationInstances.add(new FederationInstance(federation.getId(), federation.getName(), 
                        federation.getDescription(), federation.enabled(), federation.getOwner()));
            }
            
            return federationInstances;
        } else {
            throw new UnauthorizedRequestException(Messages.Exception.FHS_IS_NOT_AUTHORIZED_TO_PERFORM_ACTION);
        }
    }

    public void updateRemoteFederationList(String fhsId, List<FederationInstance> remoteFederationInstances) throws FogbowException {
        if (this.allowedFhsIds.contains(fhsId)) {
            List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
            
            for (FederationInstance federationInstance : remoteFederationInstances) {
                remoteFederations.add(new RemoteFederation(federationInstance.getFedId(),
                        federationInstance.getFedName(), federationInstance.getDescription(),
                        federationInstance.isEnabled(), federationInstance.getOwningFedAdminId(), fhsId));
            }
            
            this.federationHost.updateRemoteFederationList(fhsId, remoteFederations);
        } else {
            throw new UnauthorizedRequestException(Messages.Exception.FHS_IS_NOT_AUTHORIZED_TO_PERFORM_ACTION);
        }
    }

    public Federation joinFederation(String fhsId, FederationUser requester, String federationId) throws FogbowException {
        if (this.allowedFhsIds.contains(fhsId)) {
            return this.federationHost.joinRemoteFederation(requester, fhsId, federationId);
        }
        
        throw new UnauthorizedRequestException(Messages.Exception.FHS_IS_NOT_AUTHORIZED_TO_PERFORM_ACTION);
    }
}
