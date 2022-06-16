package cloud.fogbow.fhs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.core.ApplicationFacade;
import cloud.fogbow.fhs.core.AuthorizationPluginInstantiator;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.CommunicationMechanismInstantiator;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanismInstantiator;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.utils.SynchronizationManager;

@Component
public class Main implements ApplicationRunner {
    public static final String PROPERTY_NAME_OPERATOR_ID_SEPARATOR = "_";

    @Autowired
    private DatabaseManager databaseManager;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String publicKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PUBLIC_KEY_FILE_PATH);
        String privateKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PRIVATE_KEY_FILE_PATH);
        ServiceAsymmetricKeysHolder.getInstance().setPublicKeyFilePath(publicKeyFilePath);
        ServiceAsymmetricKeysHolder.getInstance().setPrivateKeyFilePath(privateKeyFilePath);
        
        String className = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY);
        AuthorizationPlugin<FhsOperation> authorizationPlugin = AuthorizationPluginInstantiator.getAuthorizationPlugin(className);
        FederationHost federationHost = new FederationHost(databaseManager);
        List<FederationUser> fhsOperators = ApplicationFacade.loadFhsOperatorsOrFail();
        FederationAuthenticationPluginInstantiator authenticationPluginInstantiator = new FederationAuthenticationPluginInstantiator();
        SynchronizationManager synchronizationManager = new SynchronizationManager();
        String communicationMechanismClassName = 
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.COMMUNICATION_MECHANISM_CLASS_NAME);
        FhsCommunicationMechanism fhsCommunicationMechanism = 
                CommunicationMechanismInstantiator.getCommunicationMechanism(communicationMechanismClassName);
        String synchronizationMechanismClassName = 
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.SYNCHRONIZATION_MECHANISM_CLASS_NAME);
        SynchronizationMechanism synchronizationMechanism = SynchronizationMechanismInstantiator.getSynchronizationMechanism(
                synchronizationMechanismClassName, federationHost, fhsCommunicationMechanism);
        
        synchronizationMechanism.onStartUp();
        
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setFhsOperators(fhsOperators);
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
        applicationFacade.setAuthenticationPluginInstantiator(authenticationPluginInstantiator);
        applicationFacade.setDatabaseManager(databaseManager);
        applicationFacade.setSynchronizationManager(synchronizationManager);
        applicationFacade.setFhsCommunicationMechanism(fhsCommunicationMechanism);
        
        RemoteFacade remoteFacade = RemoteFacade.getInstance();
        
        remoteFacade.setFederationHost(federationHost);
    }
}
