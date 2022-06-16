package cloud.fogbow.fhs.core.intercomponent;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.Federation;

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
}
