package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;

public class TimeBasedSynchronizationMechanism implements SynchronizationMechanism {
    private final Logger LOGGER = Logger.getLogger(TimeBasedSynchronizationMechanism.class);
    
    private List<FederationUpdate> localUpdates;
    private List<FederationUpdate> remoteUpdates;
    private Thread updateThread;
    private FederationHost federationHost;
    private FhsCommunicationMechanism communicationMechanism;
    private List<String> allowedFhssIds;
    private long sleepTime;
    private String localFhsId;
    
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
        
        String sleepTimeString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.SYNCHRONIZATION_SLEEP_TIME);
        
        if (sleepTimeString == null) {
            throw new ConfigurationErrorException(Messages.Exception.MISSING_SYNCHRONIZATION_SLEEP_TIME_PROPERTY);
        }

        this.sleepTime = Long.valueOf(sleepTimeString);
        
        this.localFhsId = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.PROVIDER_ID_KEY);
        
        if (this.localFhsId == null) {
            throw new ConfigurationErrorException(Messages.Exception.MISSING_PROVIDER_ID_PROPERTY);
        }
    }
    
    @VisibleForTesting
    List<String> getAllowedFhssIds() {
        return this.allowedFhssIds;
    }
    
    @VisibleForTesting
    long getSleepTime() {
        return this.sleepTime;
    }
    
    @Override
    public void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism) {
        this.communicationMechanism = communicationMechanism;
    }

    @Override
    public void onStartUp() throws Exception {
        if (updateThread == null) {
            FederationUpdateDaemon daemon = new FederationUpdateDaemon(
                    this.localUpdates, this.remoteUpdates, this.communicationMechanism, 
                    this.federationHost, this.sleepTime, this.allowedFhssIds, this.localFhsId);
            this.updateThread = new Thread(daemon);
            this.updateThread.start();
        } else {
            LOGGER.error(Messages.Exception.SYNCHRONIZATION_MECHANISM_HAS_ALREADY_BEEN_STARTED_UP);
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
