package cloud.fogbow.fhs.api.http.response;

import java.util.List;

public class FederationMember {
    private String memberId;
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    private List<String> attributes;
    
    public FederationMember(String memberId, String name, String email, String description, boolean enabled, 
            List<String> attributes) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
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
    
    public List<String> getAttributes() {
        return attributes;
    }
}
