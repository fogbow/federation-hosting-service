package cloud.fogbow.fhs.core.intercomponent;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

// TODO documentation
public interface FhsCommunicationMechanism {
    List<FederationInstance> getRemoteFederations(String hostId) throws FogbowException;
    List<FederationInstance> syncFederations(String remoteFedHost, List<FederationInstance> localFederations) throws FogbowException;
    Federation joinRemoteFederation(FederationUser requester, String federationId, String ownerFhsId) throws FogbowException;
}
