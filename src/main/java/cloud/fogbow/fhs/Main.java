package cloud.fogbow.fhs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import cloud.fogbow.fhs.core.AuthorizationPluginInstantiator;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;

// TODO refactor
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
        FederationHost federationHost = new FederationHost();
        
        List<FederationUser> fhsOperators = new ArrayList<>();
        
        String operatorIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY);
        
        if (operatorIdsListString.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.NO_OPERATOR_ID_SPECIFIED);
        } else {
            List<String> fhsOperatorUserIds = Arrays.asList(operatorIdsListString.split(
                    SystemConstants.OPERATOR_IDS_SEPARATOR));
            
            for (String fhsOperatorUserId : fhsOperatorUserIds) {
                Map<String, String> fhsOperatorAuthenticationProperties = new HashMap<String, String>();
                Properties properties = PropertiesHolder.getInstance().getProperties();
                
                for (Object keyProperties : properties.keySet()) {
                    String keyPropertiesStr = keyProperties.toString();
                    if (keyPropertiesStr.startsWith(fhsOperatorUserId + "_")) {
                        String value = properties.getProperty(keyPropertiesStr);
                        String key = normalizeKeyProperties(fhsOperatorUserId, keyPropertiesStr);
                        fhsOperatorAuthenticationProperties.put(key, value);
                    }
                }

                FederationUser operator = new FederationUser(fhsOperatorUserId, "", "", "", 
                        true, fhsOperatorAuthenticationProperties);
                fhsOperators.add(operator);
            }
        }

        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setFhsOperators(fhsOperators);
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
    }
    

    private String normalizeKeyProperties(String fhsOperatorUserId, String keyPropertiesStr) {
        return keyPropertiesStr.replace(fhsOperatorUserId + "_", "");
    }
}
