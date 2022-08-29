package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FederationDescription {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.FEDERATION_ID, notes = ApiDocumentation.Model.FEDERATION_ID_NOTE)
    private String id;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.FEDERATION_NAME, notes = ApiDocumentation.Model.FEDERATION_NAME_NOTE)
    private String name;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.FEDERATION_DESCRIPTION)
    private String description;

    public FederationDescription(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
