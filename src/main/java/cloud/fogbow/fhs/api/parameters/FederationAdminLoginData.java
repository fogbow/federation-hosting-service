package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FederationAdminLoginData {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.ADMIN_ID)
    private String federationAdminId;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.CREDENTIALS)
    private Map<String, String> credentials;
    
    public FederationAdminLoginData() {
        
    }

    public String getFederationAdminId() {
        return federationAdminId;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }
}
