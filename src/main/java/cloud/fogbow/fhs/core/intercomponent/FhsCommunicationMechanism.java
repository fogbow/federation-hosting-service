package cloud.fogbow.fhs.core.intercomponent;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;

// TODO documentation
public interface FhsCommunicationMechanism {
    List<FederationInstance> getRemoteFederations(String hostId) throws FogbowException;
    List<FederationInstance> syncFederations(String remoteFedHost, List<FederationInstance> localFederations) throws FogbowException;
}
