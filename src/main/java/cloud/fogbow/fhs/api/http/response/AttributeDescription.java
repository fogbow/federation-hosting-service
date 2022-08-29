package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class AttributeDescription {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.ATTRIBUTE_ID)
    private String id;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.ATTRIBUTE_NAME)
    private String name;
    
    public AttributeDescription(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
