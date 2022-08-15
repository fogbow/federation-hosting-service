package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.List;

import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;

public class RemoteUpdateHandler {
    private final Logger LOGGER = Logger.getLogger(RemoteUpdateHandler.class);
    
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
        
        if (federation.getFhsId().equals(localFhsId)) {
            updateLocalFederation(update, federation);
        } else {
            updateRemoteFederation(update);
        }
    }

    // if updated federation is local, update all supporting fhss
    private void updateLocalFederation(FederationUpdate update, Federation federation)
            throws InvalidParameterException {
        federationHost.updateFederationUsingRemoteData(update);

        List<String> supportingFhss = federation.getSupportingFhss();
        
        for (String supportingFhs : supportingFhss) {
            if (!update.getUpdatedFhss().contains(supportingFhs)) {
                try {
                    communicationMechanism.updateFederation(supportingFhs, update);
                    update.addUpdatedFhs(supportingFhs);
                } catch (FogbowException e) {
                    LOGGER.error(String.format(Messages.Exception.FAILED_TO_UPDATE_FEDERATION_IN_FHS, supportingFhs));
                }
            }
        }
        
        if (update.getUpdatedFhss().equals(supportingFhss)) {
            update.setAsCompleted();
        }
    }

    // if updated federation is remote, update locally only
    private void updateRemoteFederation(FederationUpdate update) throws InvalidParameterException {
        federationHost.updateFederationUsingRemoteData(update);
        update.setAsCompleted();
    }
}
