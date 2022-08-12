package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
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
    private List<String> allowedFhssIds;
    private long sleepTime;
    
    public TimeBasedSynchronizationMechanism(FederationHost federationHost) throws ConfigurationErrorException {
        this.federationHost = federationHost;
        // TODO should load from storage
        this.localUpdates = new ArrayList<FederationUpdate>();
        this.remoteUpdates = new ArrayList<FederationUpdate>();
        this.allowedFhssIds = new ArrayList<String>();
        
        String allowedFhsIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY);
        
        if (allowedFhsIdsListString == null) { 
            throw new ConfigurationErrorException(Messages.Exception.MISSING_ALLOWED_FHS_IDS_PROPERTY);
        }
        
        if (!allowedFhsIdsListString.isEmpty()) {
            for (String allowedFhsId :  allowedFhsIdsListString.split(
                    SystemConstants.ALLOWED_FHS_IDS_SEPARATOR)) {
                this.allowedFhssIds.add(allowedFhsId);
            }
        }
        
        // FIXME constant
        this.sleepTime = Long.valueOf(PropertiesHolder.getInstance().getProperty(
                "synchronization_sleep_time"));
    }
    
    @Override
    public void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism) {
        this.communicationMechanism = communicationMechanism;
    }

    @Override
    public void onStartUp() throws Exception {
        if (updateThread == null) {
            this.updateThread = new Thread(new FederationUpdateDaemon(localUpdates, 
                    remoteUpdates, communicationMechanism, federationHost, 
                    this.sleepTime, this.allowedFhssIds));
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
