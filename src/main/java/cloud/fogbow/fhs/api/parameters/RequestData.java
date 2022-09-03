package cloud.fogbow.fhs.api.parameters;

import java.util.List;
import java.util.Map;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class RequestData {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.PATH, notes = ApiDocumentation.Model.PATH_NOTE)
    private List<String> path;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.HEADERS, notes = ApiDocumentation.Model.HEADERS_NOTE)
    private Map<String, String> headers;
    @ApiModelProperty(position = 2, required = true, example = ApiDocumentation.Model.BODY, notes = ApiDocumentation.Model.BODY_NOTE)
    private Map<String, Object> body;
    
    public RequestData() {
        
    }
    
    public RequestData(List<String> path, Map<String, String> headers, Map<String, Object> body) {
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public List<String> getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getBody() {
        return body;
    }    
}
