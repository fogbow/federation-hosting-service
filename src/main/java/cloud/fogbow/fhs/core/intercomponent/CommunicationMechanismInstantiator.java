package cloud.fogbow.fhs.core.intercomponent;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class CommunicationMechanismInstantiator {
    private static FhsClassFactory classFactory = new FhsClassFactory();
    
    public static FhsCommunicationMechanism getCommunicationMechanism(String className) {
        return (FhsCommunicationMechanism) 
                CommunicationMechanismInstantiator.classFactory.createPluginInstance(className);
    }
}
