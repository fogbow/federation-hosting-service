package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FederationUser {
    private String id;
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    private List<String> attributes;
    
    public FederationUser(String id, String name, String email, String description, boolean enabled) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = new ArrayList<String>();
    }
    
    public FederationUser(String name, String email, String description, boolean enabled) {
        this(UUID.randomUUID().toString(), name, email, description, enabled);
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

    // TODO test
    public void addAttribute(String attributeId) {
        // TODO should check if attributeId has already been added
        this.attributes.add(attributeId);
    }

    // TODO test
    public void removeAttribute(String attributeId) {
        // TODO should check if attributeId has already been added 
        this.attributes.remove(attributeId);
    }

    public List<String> getAttributes() {
        return this.attributes;
    }
}
