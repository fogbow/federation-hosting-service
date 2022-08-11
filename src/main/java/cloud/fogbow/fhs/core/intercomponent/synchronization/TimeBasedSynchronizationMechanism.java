package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;

// TODO test
public class TimeBasedSynchronizationMechanism implements SynchronizationMechanism {
    private List<FederationUpdate> localUpdates;
    private List<FederationUpdate> remoteUpdates;
    private Thread updateThread;
    private FederationHost federationHost;
    private FhsCommunicationMechanism communicationMechanism;
    
    public TimeBasedSynchronizationMechanism(FederationHost federationHost) throws ConfigurationErrorException {
        this.federationHost = federationHost;
        // TODO should load from storage
        this.localUpdates = new ArrayList<FederationUpdate>();
        this.remoteUpdates = new ArrayList<FederationUpdate>();
    }
    
    @Override
    public void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism) {
        this.communicationMechanism = communicationMechanism;
    }

    @Override
    public void onStartUp() throws Exception {
        if (updateThread == null) {
            this.updateThread = new Thread(new FederationUpdateDaemon(localUpdates, 
                    remoteUpdates, communicationMechanism, federationHost));
            updateThread.start();
        } else {
            // TODO error
        }
    }

    @Override
    public void onLocalUpdate(FederationUpdate updatedFederation) {
        synchronized(localUpdates) {
            this.localUpdates.add(updatedFederation);
        }
    }

    @Override
    public void onRemoteUpdate(FederationUpdate updatedFederation) {
        synchronized(remoteUpdates) {
            this.remoteUpdates.add(updatedFederation);
        }
    }
}
