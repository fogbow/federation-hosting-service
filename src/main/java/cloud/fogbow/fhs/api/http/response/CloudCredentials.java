package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class CloudCredentials {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.CLOUD_CREDENTIALS, notes = ApiDocumentation.Model.CLOUD_CREDENTIALS_NOTE)
    private Map<String, String> credentials;

    public CloudCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }
}
