package cloud.fogbow.fhs.core.plugins.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceOperation;

// TODO documentation
// TODO this class should use a more friendly format for the rules, such as XML
public class DefaultServiceAccessPolicy implements ServiceAccessPolicy {
    public static final String ACCESS_LEVELS_SEPARATOR = ";";
    private static final String ACCESS_LEVELS_FIELDS_SEPARATOR = ":";
    private static final String ACCESS_LEVEL_OPERATIONS_SEPARATOR = "-";
    private static final String ACCESS_LEVEL_OPERATION_FIELDS_SEPARATOR = ",";
    private static final String ACCESS_LEVEL_ATTRIBUTES_SEPARATOR = ",";
    private static final String ACCESS_LEVEL_CLOUD_CREDENTIALS_FIELD_SEPARATOR = "=";
    private static final String ACCESS_LEVEL_CLOUD_CREDENTIALS_PAIRS_SEPARATOR = "-";
    private static final String CREDENTIALS_PAIR_SEPARATOR = "&";

    private Map<String, AccessLevel> accessLevels;
    private Map<Pair<String, AccessLevel>, Map<String, String>> credentialsByCloudAndAccessLevel;
    
    /*
     * Rule string format
     * 
     *  access_level
     *     name
     *     operations
     *        operation
     *           method
     *           path
     *     attributes
     *        attribute
     *     clouds
     *        cloud
     *           credentials
     *              credential
     * 
     * access_level_name_0:access_definition_0;access_level_name_1:access_definition_1
     * access_definition -> operations=method_0,path_0+method_1,path_1:attributes=attribute_0,attribute_1:clouds=cloud_name+credential_name_0,credential_value_0+credential_name_1:credential_value_1 
     */
    
    public DefaultServiceAccessPolicy(String rulesString) {
        this.accessLevels = new HashMap<String, AccessLevel>();
        this.credentialsByCloudAndAccessLevel = new HashMap<Pair<String, AccessLevel>, Map<String, String>>();
        
        String[] accessLevelsStrings = rulesString.split(ACCESS_LEVELS_SEPARATOR);
        
        for (String accessLevelString : accessLevelsStrings) {
            extractAccessLevel(accessLevelString);
        }
    }

    private void extractAccessLevel(String accessLevelString) {
        String[] accessLevelFields = accessLevelString.split(ACCESS_LEVELS_FIELDS_SEPARATOR);
        
        String accessLevelName = accessLevelFields[0];
        String accessLevelOperationsString = accessLevelFields[1];
        String accessLevelAttributesString = accessLevelFields[2];
        String cloudCredentialsString = accessLevelFields[3];
        
        List<ServiceOperation> operations = extractOperations(accessLevelOperationsString);
        AccessLevel accessLevel = new AccessLevel(accessLevelName, operations);
        
        extractAttributes(accessLevel, accessLevelAttributesString);
        extractCredentials(accessLevel, cloudCredentialsString);
    }

    private List<ServiceOperation> extractOperations(String operationsString) {
        String[] operationsStrings = operationsString.split(ACCESS_LEVEL_OPERATIONS_SEPARATOR);
        List<ServiceOperation> operations = new ArrayList<ServiceOperation>();
        
        for (String operationString : operationsStrings) {
            HttpMethod method = HttpMethod.valueOf(
                    operationString.split(ACCESS_LEVEL_OPERATION_FIELDS_SEPARATOR)[0]);
            ServiceOperation operation = new ServiceOperation(method);
            operations.add(operation);
        }
        return operations;
    }

    private void extractAttributes(AccessLevel accessLevel, String attributesString) {
        for (String attribute : attributesString.split(ACCESS_LEVEL_ATTRIBUTES_SEPARATOR)) {
            this.accessLevels.put(attribute, accessLevel);
        }
    }

    private void extractCredentials(AccessLevel accessLevel, String allCloudsCredentialsString) {
        for (String cloudCredentialsString : allCloudsCredentialsString.split(ACCESS_LEVEL_OPERATION_FIELDS_SEPARATOR)) {
            extractCloudCredentials(accessLevel, cloudCredentialsString);
        }
    }

    private void extractCloudCredentials(AccessLevel accessLevel, String cloudCredentialsString) {
        String[] cloudCredentialsFields = cloudCredentialsString.split(ACCESS_LEVEL_CLOUD_CREDENTIALS_FIELD_SEPARATOR);
        
        String cloudName = cloudCredentialsFields[0];
        String[] cloudCredentialsPairs = cloudCredentialsFields[1].split(ACCESS_LEVEL_CLOUD_CREDENTIALS_PAIRS_SEPARATOR);
        
        Map<String, String> credentialsMap = new HashMap<String, String>();
        
        for (String cloudCredentialPair : cloudCredentialsPairs) {
            String[] credentialPairFields = cloudCredentialPair.split(CREDENTIALS_PAIR_SEPARATOR);
            String credentialKey = credentialPairFields[0];
            String credentialValue = credentialPairFields[1];
            
            credentialsMap.put(credentialKey, credentialValue);
        }
        
        this.credentialsByCloudAndAccessLevel.put(Pair.of(cloudName, accessLevel), credentialsMap);
    }
    
    @Override
    public Map<String, String> getCredentialsForAccess(FederationUser user, String cloudName) {
        List<String> userAttributes = user.getAttributes();
        // FIXME should check if user has attributes
        AccessLevel level = accessLevels.get(userAttributes.get(0));
        // FIXME should check if access level is not null and try again if so
        return this.credentialsByCloudAndAccessLevel.get(Pair.of(cloudName, level));
    }

    @Override
    public boolean isAllowedToPerform(FederationUser user, ServiceOperation operation) {
        List<String> userAttributes = user.getAttributes();
        
        for (String userAttribute : userAttributes) {
            AccessLevel level = accessLevels.get(userAttribute); 
            
            if (level != null && level.getAllowedOperations().contains(operation)) {
                return true;
            }
        }
            
        return false;
    }
    
    @VisibleForTesting
    Map<String, AccessLevel> getAccessLevels() {
        return accessLevels;
    }

    @VisibleForTesting
    Map<Pair<String, AccessLevel>, Map<String, String>> getCredentialsByCloudAndAccessLevel() {
        return credentialsByCloudAndAccessLevel;
    }
}
