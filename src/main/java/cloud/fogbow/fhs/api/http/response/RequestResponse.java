package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class RequestResponse {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.REQUEST_RESPONSE_CODE, notes = ApiDocumentation.Model.REQUEST_RESPONSE_CODE_NOTE)
    private int responseCode;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.REQUEST_RESPONSE_DATA, notes = ApiDocumentation.Model.REQUEST_RESPONSE_DATA_NOTE)
    private Map<String, String> responseData;
    
    public RequestResponse(int responseCode, Map<String, String> responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, String> getResponseData() {
        return responseData;
    }
}
