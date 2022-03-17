package cloud.fogbow.fhs;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.core.ApplicationFacade;
import cloud.fogbow.fhs.core.AuthorizationPluginInstantiator;
import cloud.fogbow.fhs.core.LocalFederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FhsOperation;

@Component
public class Main implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String publicKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PUBLIC_KEY_FILE_PATH);
        String privateKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PRIVATE_KEY_FILE_PATH);
        ServiceAsymmetricKeysHolder.getInstance().setPublicKeyFilePath(publicKeyFilePath);
        ServiceAsymmetricKeysHolder.getInstance().setPrivateKeyFilePath(privateKeyFilePath);
        
        String className = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY);
        AuthorizationPlugin<FhsOperation> authorizationPlugin = AuthorizationPluginInstantiator.getAuthorizationPlugin(className);
        LocalFederationHost localFederationHost = new LocalFederationHost();
        
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(localFederationHost);
    }
}
