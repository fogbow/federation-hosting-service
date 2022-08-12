package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.util.StoppableRunner;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;

// TODO test
// TODO refactor
public class FederationUpdateDaemon extends StoppableRunner implements Runnable {
    private final Logger LOGGER = Logger.getLogger(FederationUpdateDaemon.class);
    
    private List<FederationUpdate> localUpdates;
    private List<FederationUpdate> remoteUpdates;
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private String localFhsId;
    private List<String> remoteFedHosts;
    
    public FederationUpdateDaemon(List<FederationUpdate> localUpdates, List<FederationUpdate> remoteUpdates, 
            FhsCommunicationMechanism communicationMechanism, FederationHost federationHost) throws ConfigurationErrorException {
        // FIXME constant
        super(5000L);
        this.localUpdates = localUpdates;
        this.remoteUpdates = remoteUpdates;
        this.federationHost = federationHost;
        this.communicationMechanism = communicationMechanism;
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

    private void synchronizeFederationInstances() {
        List<FederationInstance> localFederations = getLocalFederationInstances();
        List<RemoteFederation> remoteFederations = new ArrayList<RemoteFederation>();
        
        for (String remoteFedHost : remoteFedHosts) {
            remoteFederations.addAll(tryToGetRemoteFederations(remoteFedHost, localFederations));
        }
        
        federationHost.setRemoteFederationsList(remoteFederations);
    }
    
    private void synchronizeLocalUpdates() {
        synchronized(this.localUpdates) {
            List<FederationUpdate> completedUpdates = new ArrayList<FederationUpdate>();
            
            for (FederationUpdate localUpdate : this.localUpdates) {
                Federation federation = getFederation(localUpdate.getTargetFederationId());
                
                // if updated federation is local, update all supporting fhss
                if (federation.getFhsId().equals(localFhsId)) {
                    List<String> supportingFhss = getSupportingFhss(federation); 
                    
                    for (String supportingFhs : supportingFhss) {
                        if (!localUpdate.getUpdatedFhss().contains(supportingFhs)) {
                            try {
                                communicationMechanism.updateFederation(supportingFhs, localUpdate);
                                localUpdate.addUpdatedFhs(supportingFhs);
                            } catch (FogbowException e) {
                                // TODO logging
                            }
                        }
                    }
                    
                    if (localUpdate.getUpdatedFhss().equals(supportingFhss)) {
                        localUpdate.setAsCompleted();
                        completedUpdates.add(localUpdate);
                    }
                } else {
                    // if updated federation is remote, update only the owner
                    try {
                        communicationMechanism.updateFederation(federation.getFhsId(), localUpdate);
                        localUpdate.setAsCompleted();
                        completedUpdates.add(localUpdate);
                    } catch (FogbowException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            
            this.localUpdates.removeAll(completedUpdates);
        }
    }

    private void synchronizeRemoteUpdates() {
        synchronized(this.remoteUpdates) {
            List<FederationUpdate> completedUpdates = new ArrayList<FederationUpdate>();
            
            for (FederationUpdate remoteUpdate : this.remoteUpdates) {
                Federation federation = getFederation(remoteUpdate.getTargetFederationId());
                
                // if updated federation is local, update all supporting fhss
                if (federation.getFhsId().equals(localFhsId)) {
                    List<String> supportingFhss = getSupportingFhss(federation); 
                    
                    try {
                        federationHost.updateFederationUsingRemoteData(remoteUpdate);
                    } catch (InvalidParameterException e1) {
                        // TODO error
                        e1.printStackTrace();
                    }

                    for (String supportingFhs : supportingFhss) {
                        if (!remoteUpdate.getUpdatedFhss().contains(supportingFhs)) {
                            try {
                                communicationMechanism.updateFederation(supportingFhs, remoteUpdate);
                                remoteUpdate.addUpdatedFhs(supportingFhs);
                            } catch (FogbowException e) {
                                // TODO logging
                            }
                        }
                    }
                    
                    if (remoteUpdate.getUpdatedFhss().equals(supportingFhss)) {
                        remoteUpdate.setAsCompleted();
                        completedUpdates.add(remoteUpdate);
                    }
                    
                } else {
                    // if updated federation is remote, update locally only
                    try {
                        federationHost.updateFederationUsingRemoteData(remoteUpdate);
                    } catch (InvalidParameterException e) {
                        // TODO error
                        e.printStackTrace();
                    }
                    
                    remoteUpdate.setAsCompleted();
                    completedUpdates.add(remoteUpdate);
                }
            }
            
            this.remoteUpdates.removeAll(completedUpdates);
        }
    }

    private Federation getFederation(String targetFederationId) {
        List<Federation> localFederations = this.federationHost.getFederations();
        for (Federation federation : localFederations) {
            if (federation.getId().equals(targetFederationId)) {
                return federation;
            }
        }
        
        // TODO error
        return null;
    }

    private List<String> getSupportingFhss(Federation federation) {
        List<FederationUser> remoteAdmins = federation.getRemoteAdmins();
        List<String> supportingFhss = new ArrayList<String>();
        
        for (FederationUser remoteAdmin : remoteAdmins) {
            String supportingFhs = remoteAdmin.getFhsId();
            
            if (!supportingFhs.equals(localFhsId)) {
                supportingFhss.add(supportingFhs);
            }
        }
        
        return supportingFhss;
    }

    @Override
    public void doRun() throws InterruptedException {
        synchronizeFederationInstances();
        synchronizeLocalUpdates();
        synchronizeRemoteUpdates();
    }
}
