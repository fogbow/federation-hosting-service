package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.fhs.core.models.Federation;

// TODO documentation
public interface SynchronizationMechanism {
    void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism);
    void onStartUp() throws Exception;
    void onLocalUpdate(Federation updatedFederation);
    void onRemoteUpdate(Federation updatedFederation);
}
