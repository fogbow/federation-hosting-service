package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FederationId {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.FEDERATION_NAME, notes = ApiDocumentation.Model.FEDERATION_NAME_NOTE)
    private String name;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.FEDERATION_ID, notes = ApiDocumentation.Model.FEDERATION_ID_NOTE)
    private String id;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.FEDERATION_ENABLED)
    private boolean enabled;

    public FederationId(String name, String id, boolean enabled) {
        this.name = name;
        this.id = id;
        this.enabled = enabled;
    }
    
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
