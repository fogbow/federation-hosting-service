package cloud.fogbow.fhs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

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
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;

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
        List<FederationUser> fhsOperators = loadFhsOperatorsOrFail();
        FederationAuthenticationPluginInstantiator authenticationPluginInstantiator = new FederationAuthenticationPluginInstantiator();
        
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setFhsOperators(fhsOperators);
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
        applicationFacade.setAuthenticationPluginInstantiator(authenticationPluginInstantiator);
    }

    @VisibleForTesting
    List<FederationUser> loadFhsOperatorsOrFail() throws ConfigurationErrorException {
        String operatorIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY);
        
        if (operatorIdsListString == null || operatorIdsListString.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.NO_OPERATOR_ID_SPECIFIED);
        } else {
            return loadFhsOperators(operatorIdsListString);
        }
    }

    private List<FederationUser> loadFhsOperators(String operatorIdsListString) {
        List<FederationUser> fhsOperators = new ArrayList<>();
        List<String> fhsOperatorUserIds = Arrays.asList(operatorIdsListString.split(
                SystemConstants.OPERATOR_IDS_SEPARATOR));
        
        for (String fhsOperatorUserId : fhsOperatorUserIds) {
            FederationUser operator = loadOperator(fhsOperatorUserId); 
            fhsOperators.add(operator);
        }
        
        return fhsOperators;
    }
    
    private FederationUser loadOperator(String fhsOperatorUserId) {
        Map<String, String> fhsOperatorAuthenticationProperties = new HashMap<String, String>();
        Properties properties = PropertiesHolder.getInstance().getProperties();
        
        for (Object keyProperties : properties.keySet()) {
            String keyPropertiesStr = keyProperties.toString();
            if (keyPropertiesStr.startsWith(fhsOperatorUserId + PROPERTY_NAME_OPERATOR_ID_SEPARATOR)) {
                String value = properties.getProperty(keyPropertiesStr);
                String key = normalizeKeyProperties(fhsOperatorUserId, keyPropertiesStr);
                fhsOperatorAuthenticationProperties.put(key, value);
            }
        }

        return new FederationUser(fhsOperatorUserId, "", "", "", true, fhsOperatorAuthenticationProperties, true, false);
    }
    
    private String normalizeKeyProperties(String fhsOperatorUserId, String keyPropertiesStr) {
        return keyPropertiesStr.replace(fhsOperatorUserId + PROPERTY_NAME_OPERATOR_ID_SEPARATOR, "");
    }
}
