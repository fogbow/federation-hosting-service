package cloud.fogbow.fhs.core.plugins.authorization;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;

public class FhsOperatorAuthorizationPlugin implements AuthorizationPlugin<FhsOperation>{
    public static final List<OperationType> OPERATOR_ONLY_OPERATIONS = 
            Arrays.asList(OperationType.ADD_FED_ADMIN);
    public static final List<OperationType> OTHER_SERVICES_ADMIN_ONLY_OPERATIONS = 
            Arrays.asList(OperationType.MAP);
    private List<String> fhsOperatorUserIds;
    private List<String> otherServicesAdminIds;
    
    public FhsOperatorAuthorizationPlugin(List<String> fhsOperatorUserIds, 
            List<String> otherServicesAdminIds) {
        this.fhsOperatorUserIds = fhsOperatorUserIds;
        this.otherServicesAdminIds = otherServicesAdminIds;
    }
    
    public FhsOperatorAuthorizationPlugin() throws ConfigurationErrorException {
        String operatorIdsListString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY);
        
        if (operatorIdsListString.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.NO_OPERATOR_ID_SPECIFIED);
        } else {
            this.fhsOperatorUserIds = Arrays.asList(operatorIdsListString.split(
                    SystemConstants.OPERATOR_IDS_SEPARATOR));
        }
        
        String otherServiceAdminsString = PropertiesHolder.getInstance().getProperty(
                ConfigurationPropertyKeys.OTHER_SERVICES_ADMIN_IDS_KEY);
        
        if (otherServiceAdminsString.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.NO_OTHER_SERVICE_ADMIN_ID_SPECIFIED);
        } else {
            this.otherServicesAdminIds = Arrays.asList(otherServiceAdminsString.split(
                    SystemConstants.OTHER_SERVICES_ADMIN_IDS_SEPARATOR));
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
        return StringUtils.splitByWholeSeparator(userId, FogbowConstants.FEDERATION_ID_SEPARATOR)[0];
    }

    @VisibleForTesting
    List<String> getFhsOperatorUserIds() {
        return this.fhsOperatorUserIds;
    }
    
    @VisibleForTesting
    List<String> getOtherServicesAdminIds() {
        return this.otherServicesAdminIds;
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
