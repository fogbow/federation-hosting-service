package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class ServiceDiscovered {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.SERVICE_ID)
    private String serviceId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.SERVICE_METADATA)
    private Map<String, String> metadata;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.ENDPOINT)
    private String endpoint;
    
    public ServiceDiscovered(String serviceId, Map<String, String> metadata, String endpoint) {
        this.serviceId = serviceId;
        this.metadata = metadata;
        this.endpoint = endpoint;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
}
