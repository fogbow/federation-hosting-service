package cloud.fogbow.fhs.api.parameters;

// TODO documentation
public class FederationUser {
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    
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
}
