package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FederationUser {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.FEDERATION_USER_NAME)
    private String name;
    @ApiModelProperty(position = 1, required = false, example = ApiDocumentation.Model.FEDERATION_USER_EMAIL)
    private String email;
    @ApiModelProperty(position = 2, required = false, example = ApiDocumentation.Model.FEDERATION_USER_DESCRIPTION)
    private String description;
    @ApiModelProperty(position = 3, required = true, example = ApiDocumentation.Model.FEDERATION_USER_ENABLED)
    private boolean enabled;
    @ApiModelProperty(position = 4, required = true, example = ApiDocumentation.Model.FEDERATION_USER_AUTHENTICATION_PROPERTIES)
    private Map<String, String> authenticationProperties;
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean getEnabled() {
        return enabled;
    }

    public Map<String, String> getAuthenticationProperties() {
        return authenticationProperties;
    }
}
