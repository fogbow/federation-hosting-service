package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class LoginData {
    private String federationId;
    private String memberId;
    private Map<String, String> credentials;

    public LoginData() {
        
    }

    public String getFederationId() {
        return federationId;
    }

    public String getMemberId() {
        return memberId;
    }
    
    public Map<String, String> getCredentials() {
        return credentials;
    }
}
