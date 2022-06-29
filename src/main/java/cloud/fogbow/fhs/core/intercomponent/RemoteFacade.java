package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;

// TODO test
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
    
    public RemoteFacade() {
        this.allowedFhsIds = new ArrayList<String>();
        // FIXME constant
        String allowedFhsIdsListString = PropertiesHolder.getInstance().getProperty("allowed_fhs_ids");
        
        // TODO validate list string
        for (String allowedFhsId :  allowedFhsIdsListString.split(",")) {
            this.allowedFhsIds.add(allowedFhsId);
        }
    }
    
    public void setFederationHost(FederationHost federationHost) {
        this.federationHost = federationHost;
    }
    
    public List<FederationInstance> getFederationList(String fhsId) throws UnauthorizedRequestException {
        if (this.allowedFhsIds.contains(fhsId)) {
            List<Federation> federations = this.federationHost.getFederations();
            List<FederationInstance> federationInstances = new ArrayList<FederationInstance>();
            
            for (Federation federation : federations) {
                federationInstances.add(new FederationInstance(federation.getId(), federation.getName(), federation.getDescription(), 
                        federation.enabled(), federation.getOwner()));
            }
            
            return federationInstances;
        } else {
            // TODO add message
            throw new UnauthorizedRequestException();
        }
    }

    public void updateRemoteFederationList(String fhsId, List<FederationInstance> remoteFederationInstances) {
        if (this.allowedFhsIds.contains(fhsId)) {
            List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
            
            for (FederationInstance federationInstance : remoteFederationInstances) {
                remoteFederations.add(new RemoteFederation(federationInstance.getFedId(),
                        federationInstance.getFedName(), federationInstance.getDescription(),
                        federationInstance.isEnabled(), federationInstance.getOwningFedAdminId(), fhsId));
            }
            
            this.federationHost.updateRemoteFederationList(fhsId, remoteFederations);
        }
    }

    public Federation joinFederation(String fhsId, FederationUser requester, String federationId) throws FogbowException {
        if (this.allowedFhsIds.contains(fhsId)) {
            return this.federationHost.joinRemoteFederation(requester, fhsId, federationId);
        }
        
        // TODO constant
        throw new UnauthorizedRequestException("fhs is not authorized.");
    }
}
