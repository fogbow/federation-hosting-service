package cloud.fogbow.fhs.api.http.response;

public class FederationMember {
    private String memberId;
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    
    public FederationMember(String memberId, String name, String email, String description, boolean enabled) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
