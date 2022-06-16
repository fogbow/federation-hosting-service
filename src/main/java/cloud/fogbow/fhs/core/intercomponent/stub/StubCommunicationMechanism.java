package cloud.fogbow.fhs.core.intercomponent.stub;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.RemoteRequestSpecification;

public class StubCommunicationMechanism implements FhsCommunicationMechanism {

    @Override
    public String sendRequest(RemoteRequestSpecification request) throws FogbowException {
        return "";
    }
}
