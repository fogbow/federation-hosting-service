package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FederationSpec {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.FEDERATION_NAME)
    private String name;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.FEDERATION_METADATA)
    private Map<String, String> metadata;
    @ApiModelProperty(position = 2, required = false)
    private String description;
    @ApiModelProperty(position = 3, required = true)
    private boolean enabled;
    
    public FederationSpec() {
        
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
