package cloud.fogbow.fhs.core.intercomponent.stub;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

public class StubCommunicationMechanism implements FhsCommunicationMechanism {

    @Override
    public List<FederationInstance> getRemoteFederations(String hostId) throws FogbowException {
        return null;
    }

    @Override
    public List<FederationInstance> syncFederations(String remoteFedHost, List<FederationInstance> localFederations)
            throws FogbowException {
        return null;
    }

    @Override
    public Federation joinRemoteFederation(FederationUser requester, String federationId, String ownerFhsId) throws FogbowException {
        return null;
    }

    @Override
    public void updateFederation(String remoteHost, FederationUpdate update) throws FogbowException {
        
    }
}
