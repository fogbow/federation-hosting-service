package cloud.fogbow.fhs.core.plugins.authorization;

import java.util.Arrays;
import java.util.List;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;

// TODO test
public class FhsOperatorAuthorizationPlugin implements AuthorizationPlugin<FhsOperation>{

    private List<String> fhsOperatorUserIds;
    private List<String> otherServicesAdminIds;
    private static final List<OperationType> OPERATOR_ONLY_OPERATIONS = Arrays.asList(OperationType.ADD_FED_ADMIN);
    private static final List<OperationType> OTHER_SERVICES_ADMIN_ONLY_OPERATIONS = Arrays.asList(OperationType.MAP);
    
    public FhsOperatorAuthorizationPlugin() throws ConfigurationErrorException {
        // TODO constant
        String operatorIdsListString = PropertiesHolder.getInstance().getProperty("operator_ids");
        
        if (operatorIdsListString.isEmpty()) {
            // TODO constant
            throw new ConfigurationErrorException("no operator defined in the configuration file");
        } else {
            // TODO constant
            this.fhsOperatorUserIds = Arrays.asList(operatorIdsListString.split(","));
        }
        
        // TODO constant
        String otherServiceAdminString = PropertiesHolder.getInstance().getProperty("other_services_admin_ids");
        
        if (otherServiceAdminString.isEmpty()) {
            // TODO constant
            throw new ConfigurationErrorException("no services admins defined in the configuration file");
        } else {
            // TODO constant
            this.otherServicesAdminIds = Arrays.asList(operatorIdsListString.split(","));
        }
    }
    
    @Override
    public boolean isAuthorized(SystemUser systemUser, FhsOperation operation) throws UnauthorizedRequestException {
        if (OPERATOR_ONLY_OPERATIONS.contains(operation.getOperationType()) && 
                !isOperator(systemUser)) {
            throw new UnauthorizedRequestException();
        }
        
        if (OTHER_SERVICES_ADMIN_ONLY_OPERATIONS.contains(operation.getOperationType()) && 
                !isOperatorOrOtherServiceAdmin(systemUser)) {
            throw new UnauthorizedRequestException();
        }
        
        return true;
    }

    private boolean isOperator(SystemUser systemUser) {
        return this.fhsOperatorUserIds.contains(removeFederationInfo(systemUser.getId()));
    }

    private boolean isOperatorOrOtherServiceAdmin(SystemUser systemUser) {
        return this.otherServicesAdminIds.contains(removeFederationInfo(systemUser.getId())) || 
                isOperator(systemUser);
    }
    
    private String removeFederationInfo(String userId) {
        return userId.split(FogbowConstants.FEDERATION_ID_SEPARATOR)[0];
    }

    @Override
    public void setPolicy(String policy) throws ConfigurationErrorException {
        // TODO implement
    }

    @Override
    public void updatePolicy(String policy) throws ConfigurationErrorException {
        // TODO implement
    }
}
