package cloud.fogbow.fhs.api.parameters;

public class FederationUserUpdate {
    private String memberName;
    private String email;
    private String description;
    private Boolean enabled;
    
    public FederationUserUpdate() {
        
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
