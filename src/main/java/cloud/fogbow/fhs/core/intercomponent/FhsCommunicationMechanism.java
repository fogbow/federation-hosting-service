package cloud.fogbow.fhs.core.intercomponent;

public interface FhsCommunicationMechanism {
    String sendRequest(RemoteRequest request);
}
