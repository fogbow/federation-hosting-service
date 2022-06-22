package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.RemoteFederation;

public class EventBasedSynchronizationMechanism implements SynchronizationMechanism {
    private final Logger LOGGER = Logger.getLogger(EventBasedSynchronizationMechanism.class);
    
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private List<String> remoteFedHosts;
    
    // TODO test
    public EventBasedSynchronizationMechanism(FederationHost federationHost) {
        this.federationHost = federationHost;
        
        this.remoteFedHosts = new ArrayList<String>();
        // FIXME constant
        String allowedFhsIdsListString = PropertiesHolder.getInstance().getProperty("allowed_fhs_ids");
        
        if (allowedFhsIdsListString != null && !allowedFhsIdsListString.isEmpty()) {
            // FIXME constant
            for (String allowedFhsId :  allowedFhsIdsListString.split(",")) {
                this.remoteFedHosts.add(allowedFhsId);
            }
        }
    }
    
    // TODO test
    @Override
    public void onStartUp() throws FogbowException {
        List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
        
        for (String remoteFedHost : remoteFedHosts) {
            try {
                List<FederationInstance> localFederations = new ArrayList<FederationInstance>();
                
                for (Federation federation : federationHost.getFederations()) {
                    localFederations.add(new FederationInstance(federation.getId(), federation.getName(), 
                            federation.getDescription(), federation.enabled(), federation.getOwner()));
                }
                
                List<FederationInstance> remoteFederationsInstances = this.communicationMechanism.syncFederations(
                        remoteFedHost, localFederations);
                List<RemoteFederation> fhsFederations = new ArrayList<RemoteFederation>();
                
                for (FederationInstance federationInstance : remoteFederationsInstances) {
                    fhsFederations.add(new RemoteFederation(federationInstance.getFedId(),
                            federationInstance.getFedName(), federationInstance.getDescription(),
                            federationInstance.isEnabled(), federationInstance.getOwningFedAdminId(), remoteFedHost));
                }
                
                remoteFederations.addAll(fhsFederations);
            } catch (FogbowException e) {
                // FIXME constant
                LOGGER.info(String.format("Provider %s unavailable. Message: %s", remoteFedHost, e.getMessage()));
            }
        }
        
        federationHost.setRemoteFederationsList(remoteFederations);
    }

    @Override
    public void onLocalUpdate(Federation updatedFederation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRemoteUpdate(Federation updatedFederation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism) {
        this.communicationMechanism = communicationMechanism;
    }
}
