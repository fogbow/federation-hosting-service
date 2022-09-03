package cloud.fogbow.fhs.api.parameters;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FederationOwner {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.FEDERATION_OWNER)
    private String owner;
    
    public FederationOwner() {
        
    }
    
    public FederationOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
