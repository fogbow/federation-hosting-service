package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;

public class FederationUser {
    public static final String IDENTITY_PLUGIN_CLASS_NAME_KEY = "identityPluginClassName";
    
    private String id;
    private String name;
    private String federationId;
    private String email;
    private String description;
    private boolean enabled;
    private List<String> attributes;
    private String identityPluginClassName;
    private Map<String, String> identityPluginProperties;
    
    public FederationUser(String id, String name, String federationId, String email, String description, boolean enabled,
            List<String> attributes, String identityPluginClassName, Map<String, String> identityPluginProperties) {
        this.id = id;
        this.name = name;
        this.federationId = federationId;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
        this.identityPluginClassName = identityPluginClassName;
        this.identityPluginProperties = identityPluginProperties;
    }
    
    public FederationUser(String name, String federationId, String email, String description, 
            boolean enabled, Map<String, String> authenticationProperties) {
        this(UUID.randomUUID().toString(), name, federationId, email, description, enabled, new ArrayList<String>(),
                authenticationProperties.get(IDENTITY_PLUGIN_CLASS_NAME_KEY), authenticationProperties);
    }

    public String getMemberId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getFederationId() {
        return federationId;
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

    public String getIdentityPluginClassName() {
        return this.identityPluginClassName;
    }

    public Map<String, String> getIdentityPluginProperties() {
        return this.identityPluginProperties;
    }
}
