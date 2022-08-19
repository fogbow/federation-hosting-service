package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.util.StoppableRunner;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.RemoteFederation;

public class FederationUpdateDaemon extends StoppableRunner implements Runnable {
    private final Logger LOGGER = Logger.getLogger(FederationUpdateDaemon.class);
    
    private DatabaseManager databaseManager;
    private List<FederationUpdate> localUpdates;
    private List<FederationUpdate> remoteUpdates;
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private List<String> allowedFhssIds;
    private LocalUpdateHandler localUpdateHandler;
    private RemoteUpdateHandler remoteUpdateHandler;
    
    public FederationUpdateDaemon(DatabaseManager databaseManager, List<FederationUpdate> localUpdates, List<FederationUpdate> remoteUpdates, 
            FhsCommunicationMechanism communicationMechanism, FederationHost federationHost, long sleepTime,
            List<String> allowedFhssIds, String localFhsId, LocalUpdateHandler localUpdateHandler, 
            RemoteUpdateHandler remoteUpdateHandler) {
        super(sleepTime);
        this.databaseManager = databaseManager;
        this.localUpdates = localUpdates;
        this.remoteUpdates = remoteUpdates;
        this.federationHost = federationHost;
        this.communicationMechanism = communicationMechanism;
        this.allowedFhssIds = allowedFhssIds;
        this.localUpdateHandler = localUpdateHandler;
        this.remoteUpdateHandler = remoteUpdateHandler;
    }
    
    public FederationUpdateDaemon(DatabaseManager databaseManager, List<FederationUpdate> localUpdates, List<FederationUpdate> remoteUpdates, 
            FhsCommunicationMechanism communicationMechanism, FederationHost federationHost, long sleepTime,
            List<String> allowedFhssIds, String localFhsId) {
        this(databaseManager, localUpdates, remoteUpdates, communicationMechanism, federationHost, 
                sleepTime, allowedFhssIds, localFhsId,
                new LocalUpdateHandler(federationHost, communicationMechanism, localFhsId), 
                new RemoteUpdateHandler(federationHost, communicationMechanism, localFhsId));
    }

    @Override
    public void doRun() {
        synchronizeFederationInstances();
        synchronizeLocalUpdates();
        synchronizeRemoteUpdates();
    }
    
    private void synchronizeFederationInstances() {
        List<FederationInstance> localFederations = getLocalFederationInstances();
        List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
        
        for (String remoteFedHost : allowedFhssIds) {
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

    private void synchronizeLocalUpdates() {
        synchronized(this.localUpdates) {
            for (FederationUpdate localUpdate : this.localUpdates) {
                try {
                    this.localUpdateHandler.handleLocalUpdate(localUpdate);
                    
                    if (localUpdate.completed()) {
                        this.databaseManager.removeUpdate(localUpdate);
                    } else {
                        this.databaseManager.saveFederationUpdate(localUpdate);
                    }
                } catch (InvalidParameterException e) {
                    LOGGER.error(Messages.Exception.FAILED_TO_HANDLE_LOCAL_UPDATE);
                }
            }
            
            this.localUpdates.removeIf(u -> u.completed());
        }
    }

    private void synchronizeRemoteUpdates() {
        synchronized(this.remoteUpdates) {
            for (FederationUpdate remoteUpdate : this.remoteUpdates) {
                try {
                    this.remoteUpdateHandler.handleRemoteUpdate(remoteUpdate);
                    
                    if (remoteUpdate.completed()) {
                        this.databaseManager.removeUpdate(remoteUpdate);
                    } else {
                        this.databaseManager.saveFederationUpdate(remoteUpdate);
                    }
                } catch (InvalidParameterException e) {
                    LOGGER.error(Messages.Exception.FAILED_TO_HANDLE_REMOTE_UPDATE);
                }
            }
            
            this.remoteUpdates.removeIf(u -> u.completed());
        }
    }
}
