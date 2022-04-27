package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;

public class FederationUser {
    private String id;
    private String name;
    private String email;
    private String description;
    private boolean enabled;
    private List<String> attributes;
    
    public FederationUser(String id, String name, String email, String description, boolean enabled,
            List<String> attributes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
    }
    
    public FederationUser(String name, String email, String description, boolean enabled) {
        this(UUID.randomUUID().toString(), name, email, description, enabled, new ArrayList<String>());
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

    public void addAttribute(String attributeId) throws InvalidParameterException {
        if (this.attributes.contains(attributeId)) {
            throw new InvalidParameterException(
                    String.format(Messages.Exception.USER_ALREADY_HAS_ATTRIBUTE, this.id, attributeId));
        }
        
        this.attributes.add(attributeId);
    }

    public void removeAttribute(String attributeId) throws InvalidParameterException {
        if (!this.attributes.contains(attributeId)) {
            throw new InvalidParameterException(
                    String.format(Messages.Exception.USER_DOES_NOT_HAVE_ATTRIBUTE, this.id, attributeId));
        }
        
        this.attributes.remove(attributeId);
    }

    public List<String> getAttributes() {
        return this.attributes;
    }
}
