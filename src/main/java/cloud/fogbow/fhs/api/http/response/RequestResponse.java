package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

public class RequestResponse {
    private int responseCode;
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
