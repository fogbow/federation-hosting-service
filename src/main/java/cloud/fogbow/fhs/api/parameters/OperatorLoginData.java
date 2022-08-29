package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class OperatorLoginData {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.OPERATOR_ID)
    private String operatorId;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.CREDENTIALS)
    private Map<String, String> credentials;
    
    public OperatorLoginData() {
        
    }

    public String getOperatorId() {
        return operatorId;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }
}
