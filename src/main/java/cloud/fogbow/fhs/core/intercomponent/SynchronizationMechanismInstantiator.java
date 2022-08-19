package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.FhsClassFactory;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;

public class SynchronizationMechanismInstantiator {
    private static FhsClassFactory classFactory = new FhsClassFactory();

    public static SynchronizationMechanism getSynchronizationMechanism(String className, 
            DatabaseManager databaseManager, FederationHost federationHost, 
            FhsCommunicationMechanism communicationMechanism) {
        SynchronizationMechanism synchronizationMechanism = (SynchronizationMechanism) 
                SynchronizationMechanismInstantiator.classFactory.createPluginInstance(className, databaseManager, federationHost);
        synchronizationMechanism.setCommunicationMechanism(communicationMechanism);
        return synchronizationMechanism;
    }
}