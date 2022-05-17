package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

public class FederationAdminLoginData {
    private String federationAdminId;
    private Map<String, String> credentials;
    
    public FederationAdminLoginData() {
        
    }

    public String getFederationAdminId() {
        return federationAdminId;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }
}
