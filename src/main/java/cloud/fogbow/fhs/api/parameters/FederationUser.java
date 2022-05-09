package cloud.fogbow.fhs.api.parameters;

import java.util.Map;

// TODO documentation
public class FederationUser {
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    private Map<String, String> authenticationProperties;
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean getEnabled() {
        return enabled;
    }

    public Map<String, String> getAuthenticationProperties() {
        return authenticationProperties;
    }
}
