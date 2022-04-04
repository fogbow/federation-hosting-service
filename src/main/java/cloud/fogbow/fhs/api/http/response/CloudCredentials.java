package cloud.fogbow.fhs.api.http.response;

import java.util.Map;

public class CloudCredentials {
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
