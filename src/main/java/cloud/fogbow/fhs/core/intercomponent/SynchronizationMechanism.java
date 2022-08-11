package cloud.fogbow.fhs.core.intercomponent;

// TODO documentation
public interface SynchronizationMechanism {
    void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism);
    void onStartUp() throws Exception;
    void onLocalUpdate(FederationUpdate updatedFederation);
    void onRemoteUpdate(FederationUpdate updatedFederation);
}
