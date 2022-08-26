package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Service {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.OWNER_ID, notes = ApiDocumentation.Model.OWNER_ID_NOTE)
    private String ownerId;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.ENDPOINT, notes = ApiDocumentation.Model.ENDPOINT_NOTE)
    private String endpoint;
    @ApiModelProperty(position = 2, required = true, example = ApiDocumentation.Model.SERVICE_METADATA, notes = ApiDocumentation.Model.SERVICE_METADATA_NOTE)
    private Map<String, String> metadata;
    @ApiModelProperty(position = 3, required = true, example = ApiDocumentation.Model.DISCOVERY_POLICY, notes = ApiDocumentation.Model.DISCOVERY_POLICY_NOTE)
    private String discoveryPolicy;
    @ApiModelProperty(position = 4, required = true, example = ApiDocumentation.Model.ACCESS_POLICY, notes = ApiDocumentation.Model.ACCESS_POLICY_NOTE)
    private String accessPolicy;
    
    public String getOwnerId() {
        return ownerId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getDiscoveryPolicy() {
        return discoveryPolicy;
    }

    public String getAccessPolicy() {
        return accessPolicy;
    }
}
