package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class OperatorLoginData {
    private String operatorId;
    private Map<String, String> credentials;
    
    public OperatorLoginData() {
        
    }

    public String getOperatorId() {
        return operatorId;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }
}
