package cloud.fogbow.fhs.core.intercomponent.stub;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;

public class StubCommunicationMechanism implements FhsCommunicationMechanism {

    @Override
    public List<FederationInstance> getRemoteFederations(String hostId) throws FogbowException {
        // TODO Auto-generated method stub
        return null;
    }
}
