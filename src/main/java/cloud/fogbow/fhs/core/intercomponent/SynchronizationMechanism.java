package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.fhs.core.models.Federation;

public interface SynchronizationMechanism {
    void onStartUp();
    void onLocalUpdate(Federation updatedFederation);
    void onRemoteUpdate(Federation updatedFederation);
}
