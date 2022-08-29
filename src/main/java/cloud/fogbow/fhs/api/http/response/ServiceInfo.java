package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class ServiceInfo {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.SERVICE_ID)
    private String serviceId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.ENDPOINT)
    private String endpoint;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.SERVICE_METADATA)
    private Map<String, String> metadata;
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.DISCOVERY_POLICY, notes = ApiDocumentation.Model.DISCOVERY_POLICY_NOTE)
    private String discoveryPolicy;
    @ApiModelProperty(position = 4, example = ApiDocumentation.Model.ACCESS_POLICY, notes = ApiDocumentation.Model.ACCESS_POLICY_NOTE)
    private String accessPolicy;
    
    public ServiceInfo(String serviceId, String endpoint, Map<String, String> metadata, String discoveryPolicy,
            String accessPolicy) {
        this.serviceId = serviceId;
        this.endpoint = endpoint;
        this.metadata = metadata;
        this.discoveryPolicy = discoveryPolicy;
        this.accessPolicy = accessPolicy;
    }

    public String getServiceId() {
        return serviceId;
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
