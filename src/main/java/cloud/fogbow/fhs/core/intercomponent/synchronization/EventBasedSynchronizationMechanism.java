package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.RemoteRequestSpecification;
import cloud.fogbow.fhs.core.intercomponent.RequestType;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.models.Federation;

public class EventBasedSynchronizationMechanism implements SynchronizationMechanism {
    private final Logger LOGGER = Logger.getLogger(EventBasedSynchronizationMechanism.class);
    
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private List<String> remoteFedHosts;
    
    // TODO test
    public EventBasedSynchronizationMechanism(FederationHost federationHost) {
        this.federationHost = federationHost;
        
        this.remoteFedHosts = new ArrayList<String>();
        String allowedFhsIdsListString = PropertiesHolder.getInstance().getProperty("allowed_fhs_ids");
        
        // TODO validate list string
        if (allowedFhsIdsListString != null && !allowedFhsIdsListString.isEmpty()) {
            for (String allowedFhsId :  allowedFhsIdsListString.split(",")) {
                this.remoteFedHosts.add(allowedFhsId);
            }
        }
    }
    
    // TODO test
    @Override
    public void onStartUp() throws FogbowException {
        List<FederationInstance> remoteFedInstances = new ArrayList<FederationInstance>();
        
        for (String remoteFedHost : remoteFedHosts) {
            try {
                RemoteRequestSpecification spec = new RemoteRequestSpecification(
                        RequestType.GET_ALL_FEDERATIONS, new HashMap<String, Object>(), remoteFedHost);
                String response = this.communicationMechanism.sendRequest(spec);
                List<FederationInstance> remoteFederations = (List<FederationInstance>) new Gson().fromJson(response, List.class);
                remoteFedInstances.addAll(remoteFederations);
            } catch (FogbowException e) {
                // FIXME constant
                LOGGER.info(String.format("Provider %s unavailable.", remoteFedHost));
            }
        }
        
        federationHost.setRemoteFederationsList(remoteFedInstances);
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
