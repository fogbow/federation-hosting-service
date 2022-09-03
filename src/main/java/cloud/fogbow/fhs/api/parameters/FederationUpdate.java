package cloud.fogbow.fhs.api.parameters;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FederationUpdate {
    @ApiModelProperty(position = 0, required = true)
    private boolean enabled;

    public FederationUpdate() {
        
    }

    public boolean isEnabled() {
        return enabled;
    }
}
