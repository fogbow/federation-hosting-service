package cloud.fogbow.fhs.api.parameters;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AttributeSpec {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.ATTRIBUTE_NAME)
    private String name;

    public AttributeSpec() {
        
    }
    
    public AttributeSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
