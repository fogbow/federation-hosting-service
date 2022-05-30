package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;

@Entity
@Table(name = "federation_user_table")
public class FederationUser {
    public static final String IDENTITY_PLUGIN_CLASS_NAME_KEY = "identityPluginClassName";
    
    @Column(name = "member_id")
    @Id
    private String id;
    
    @Column(name = "member_name")
    private String name;
    
    @Column(name = "federation_id")
    private String federationId;
    
    @Column(name = "member_email")
    private String email;
    
    @Column(name = "member_description")
    private String description;
    
    @Column(name = "member_enabled")
    private boolean enabled;
    
    @Column(name = "member_attributes")
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> attributes;
    
    @Column(name = "identity_plugin_class_name")
    private String identityPluginClassName;
    
    @Column(name = "identity_plugin_properties")
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, String> identityPluginProperties;
    
    @Column(name = "is_operator")
    private boolean isOperator;
    
    @Column(name = "is_admin")
    private boolean isAdmin;
    
    public FederationUser() {
        
    }
    
    public FederationUser(String id, String name, String federationId, String email, String description, boolean enabled,
            List<String> attributes, String identityPluginClassName, Map<String, String> identityPluginProperties, 
            boolean isOperator, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.federationId = federationId;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
        this.identityPluginClassName = identityPluginClassName;
        this.identityPluginProperties = identityPluginProperties;
        this.isOperator = isOperator;
        this.isAdmin = isAdmin;
    }
    
    public FederationUser(String name, String federationId, String email, String description, 
            boolean enabled, Map<String, String> authenticationProperties, boolean isOperator, boolean isAdmin) {
        this(UUID.randomUUID().toString(), name, federationId, email, description, enabled, new ArrayList<String>(),
                authenticationProperties.get(IDENTITY_PLUGIN_CLASS_NAME_KEY), authenticationProperties, isOperator, isAdmin);
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
    
    public boolean isOperator() {
        return isOperator;
    }
    
    public void setAsOperator() {
        this.isOperator = true;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAsAdmin() {
        this.isAdmin = true;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
