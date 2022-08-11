package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.RemoteFederation;

public class EventBasedSynchronizationMechanism implements SynchronizationMechanism {
    private final Logger LOGGER = Logger.getLogger(EventBasedSynchronizationMechanism.class);
    
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private List<String> remoteFedHosts;
    
    public EventBasedSynchronizationMechanism(FhsCommunicationMechanism communicationMechanism, 
            FederationHost federationHost, List<String> remoteFedHosts) {
        this.communicationMechanism = communicationMechanism;
        this.federationHost = federationHost;
        this.remoteFedHosts = remoteFedHosts;
    }
    
    public EventBasedSynchronizationMechanism(FederationHost federationHost) throws ConfigurationErrorException {
        this.federationHost = federationHost;
        this.remoteFedHosts = new ArrayList<String>();
        
        String allowedFhsIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY);
        
        if (allowedFhsIdsListString == null) { 
            throw new ConfigurationErrorException(Messages.Exception.MISSING_ALLOWED_FHS_IDS_PROPERTY);
        }
        
        if (!allowedFhsIdsListString.isEmpty()) {
            for (String allowedFhsId :  allowedFhsIdsListString.split(
                    SystemConstants.ALLOWED_FHS_IDS_SEPARATOR)) {
                this.remoteFedHosts.add(allowedFhsId);
            }
        }
    }
    
    @VisibleForTesting
    List<String> getRemoteFedHosts() {
        return this.remoteFedHosts;
    }
    
    @Override
    public void onStartUp() throws FogbowException {
        List<FederationInstance> localFederations = getLocalFederationInstances();
        List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
        
        for (String remoteFedHost : remoteFedHosts) {
            remoteFederations.addAll(tryToGetRemoteFederations(remoteFedHost, localFederations));
        }
        
        federationHost.setRemoteFederationsList(remoteFederations);
    }

    private List<FederationInstance> getLocalFederationInstances() {
        List<FederationInstance> localFederations = new ArrayList<FederationInstance>();
        
        for (Federation federation : federationHost.getFederations()) {
            localFederations.add(new FederationInstance(federation.getId(), federation.getName(), 
                    federation.getDescription(), federation.enabled(), federation.getOwner()));
        }
        
        return localFederations;
    }

    private List<RemoteFederation> tryToGetRemoteFederations(String remoteFedHost, List<FederationInstance> localFederations) {
        List<RemoteFederation> fhsFederations = new ArrayList<RemoteFederation>();
        
        try {
            List<FederationInstance> remoteFederationsInstances = this.communicationMechanism.syncFederations(
                    remoteFedHost, localFederations);
            
            for (FederationInstance federationInstance : remoteFederationsInstances) {
                fhsFederations.add(new RemoteFederation(federationInstance.getFedId(),
                        federationInstance.getFedName(), federationInstance.getDescription(),
                        federationInstance.isEnabled(), federationInstance.getOwningFedAdminId(), remoteFedHost));
            }
            
        } catch (FogbowException e) {
            LOGGER.error(String.format(Messages.Exception.UNAVAILABLE_PROVIDER, remoteFedHost, e.getMessage()));
        }
        
        return fhsFederations;
    }
    
    @Override
    public void onLocalUpdate(FederationUpdate updatedFederation) {
        // TODO Implement
    }

    @Override
    public void onRemoteUpdate(FederationUpdate updatedFederation) {
        // TODO Implement
    }

    @Override
    public void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism) {
        this.communicationMechanism = communicationMechanism;
    }
}
