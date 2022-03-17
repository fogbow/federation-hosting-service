package cloud.fogbow.fhs.core.models;

import java.util.UUID;

public class FederationUser {
    private String id;
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    
    public FederationUser(String name, String email, String description, boolean enabled) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
    }

    public String getMemberId() {
        return id;
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
