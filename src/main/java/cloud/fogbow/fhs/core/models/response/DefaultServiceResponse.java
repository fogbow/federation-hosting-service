package cloud.fogbow.fhs.core.models.response;

import java.util.Map;

import cloud.fogbow.fhs.core.models.ServiceResponse;

public class DefaultServiceResponse implements ServiceResponse {
    private int code;
    private Map<String, String> response;
    
    public DefaultServiceResponse(int code, Map<String, String> response) {
        this.code = code;
        this.response = response;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Map<String, String> getResponse() {
        return response;
    }
}
