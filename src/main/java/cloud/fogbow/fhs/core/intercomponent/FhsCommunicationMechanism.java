package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.common.exceptions.FogbowException;

// TODO documentation
public interface FhsCommunicationMechanism {
    String sendRequest(RemoteRequestSpecification request) throws FogbowException;
}
