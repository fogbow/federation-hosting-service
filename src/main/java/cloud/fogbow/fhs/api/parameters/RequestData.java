package cloud.fogbow.fhs.api.parameters;

import java.util.List;
import java.util.Map;

public class RequestData {
    private List<String> path;
    private Map<String, String> headers;
    private Map<String, String> body;
    
    public RequestData() {
        
    }
    
    public RequestData(List<String> path, Map<String, String> headers, Map<String, String> body) {
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

    public Map<String, String> getBody() {
        return body;
    }    
}
