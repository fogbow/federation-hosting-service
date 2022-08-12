package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;

public class RemoteUpdateHandler {
    private FederationHost federationHost;
    private String localFhsId;
    private FhsCommunicationMechanism communicationMechanism;

    public RemoteUpdateHandler(FederationHost federationHost, FhsCommunicationMechanism communicationMechanism, 
            String localFhsId) {
        this.federationHost = federationHost;
        this.localFhsId = localFhsId;
        this.communicationMechanism = communicationMechanism;
    }

    public void handleRemoteUpdate(FederationUpdate update) throws InvalidParameterException {
        Federation federation = this.federationHost.getFederation(update.getTargetFederationId());
        
        // if updated federation is local, update all supporting fhss
        if (federation.getFhsId().equals(localFhsId)) {
            List<String> supportingFhss = federation.getSupportingFhss(); 
            
            try {
                federationHost.updateFederationUsingRemoteData(update);
            } catch (InvalidParameterException e1) {
                // TODO handle
                e1.printStackTrace();
            }

            for (String supportingFhs : supportingFhss) {
                if (!update.getUpdatedFhss().contains(supportingFhs)) {
                    try {
                        communicationMechanism.updateFederation(supportingFhs, update);
                        update.addUpdatedFhs(supportingFhs);
                    } catch (FogbowException e) {
                        // TODO handle
                        e.printStackTrace();
                    }
                }
            }
            
            if (update.getUpdatedFhss().equals(supportingFhss)) {
                update.setAsCompleted();
            }
            
        } else {
            // if updated federation is remote, update locally only
            try {
                federationHost.updateFederationUsingRemoteData(update);
            } catch (InvalidParameterException e) {
                // TODO handle
                e.printStackTrace();
            }
            
            update.setAsCompleted();
        }
    }
}
