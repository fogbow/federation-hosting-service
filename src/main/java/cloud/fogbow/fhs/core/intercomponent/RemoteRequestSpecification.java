package cloud.fogbow.fhs.core.intercomponent;

import java.util.Map;

public class RemoteRequestSpecification {
    private RequestType requestType;
    private Map<String, Object> requestParams;
    private String remoteHost;
    
    public RemoteRequestSpecification(RequestType requestType, Map<String, Object> requestParams, String remoteHost) {
        this.requestType = requestType;
        this.requestParams = requestParams;
        this.remoteHost = remoteHost;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public String getRemoteHost() {
        return remoteHost;
    }
}
