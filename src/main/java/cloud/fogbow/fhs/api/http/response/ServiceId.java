package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class ServiceId {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.SERVICE_ID)
    private String serviceId;
    
    public ServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }
}
