package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class LoginData {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.FEDERATION_ID)
    private String federationId;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.MEMBER_ID)
    private String memberId;
    @ApiModelProperty(position = 2, required = true, example = ApiDocumentation.Model.CREDENTIALS)
    private Map<String, String> credentials;

    public LoginData() {
        
    }

    public String getFederationId() {
        return federationId;
    }

    public String getMemberId() {
        return memberId;
    }
    
    public Map<String, String> getCredentials() {
        return credentials;
    }
}
