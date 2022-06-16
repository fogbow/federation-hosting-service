package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.FhsClassFactory;

public class SynchronizationMechanismInstantiator {
    private static FhsClassFactory classFactory = new FhsClassFactory();

    public static SynchronizationMechanism getSynchronizationMechanism(String className, 
            FederationHost federationHost, FhsCommunicationMechanism communicationMechanism) {
        SynchronizationMechanism synchronizationMechanism = (SynchronizationMechanism) 
                SynchronizationMechanismInstantiator.classFactory.createPluginInstance(className, federationHost);
        synchronizationMechanism.setCommunicationMechanism(communicationMechanism);
        return synchronizationMechanism;
    }
}