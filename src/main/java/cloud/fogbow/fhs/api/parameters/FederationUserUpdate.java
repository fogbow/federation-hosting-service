package cloud.fogbow.fhs.api.parameters;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FederationUserUpdate {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.FEDERATION_USER_NAME)
    private String memberName;
    @ApiModelProperty(position = 1, required = false, example = ApiDocumentation.Model.FEDERATION_USER_EMAIL)
    private String email;
    @ApiModelProperty(position = 2, required = false, example = ApiDocumentation.Model.FEDERATION_USER_DESCRIPTION)
    private String description;
    @ApiModelProperty(position = 3, required = true, example = ApiDocumentation.Model.FEDERATION_USER_ENABLED)
    private Boolean enabled;
    
    public FederationUserUpdate() {
        
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
