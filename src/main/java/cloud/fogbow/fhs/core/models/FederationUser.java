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
    private static final String MEMBER_ID_COLUMN_NAME = "member_id";
    private static final String MEMBER_NAME_COLUMN_NAME = "member_name";
    private static final String FEDERATION_ID_COLUMN_NAME = "federation_id";
    private static final String FHS_ID_COLUMN_NAME = "fhs_id";
    private static final String MEMBER_EMAIL_COLUMN_NAME = "member_email";
    private static final String MEMBER_DESCRIPTION_COLUMN_NAME = "member_description";
    private static final String MEMBER_ENABLED_COLUMN_NAME = "member_enabled";
    private static final String MEMBER_ATTRIBUTES_COLUMN_NAME = "member_attributes";
    private static final String IDENTITY_PLUGIN_CLASS_NAME_COLUMN_NAME = "identity_plugin_class_name";
    private static final String IDENTITY_PLUGIN_PROPERTIES_COLUMN_NAME = "identity_plugin_properties";
    private static final String IS_OPERATOR_COLUMN_NAME = "is_operator";
    private static final String IS_ADMIN_COLUMN_NAME = "is_admin";

    // TODO documentation
    public static final String IDENTITY_PLUGIN_CLASS_NAME_KEY = "identityPluginClassName";
    
    @Column(name = MEMBER_ID_COLUMN_NAME)
    @Id
    private String id;
    
    @Column(name = MEMBER_NAME_COLUMN_NAME)
    private String name;
    
    @Column(name = FEDERATION_ID_COLUMN_NAME)
    private String federationId;
    
    @Column(name = FHS_ID_COLUMN_NAME)
    private String fhsId;
    
    @Column(name = MEMBER_EMAIL_COLUMN_NAME)
    private String email;
    
    @Column(name = MEMBER_DESCRIPTION_COLUMN_NAME)
    private String description;
    
    @Column(name = MEMBER_ENABLED_COLUMN_NAME)
    private boolean enabled;
    
    @Column(name = MEMBER_ATTRIBUTES_COLUMN_NAME)
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> attributes;
    
    @Column(name = IDENTITY_PLUGIN_CLASS_NAME_COLUMN_NAME)
    private String identityPluginClassName;
    
    @Column(name = IDENTITY_PLUGIN_PROPERTIES_COLUMN_NAME)
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, String> identityPluginProperties;
    
    @Column(name = IS_OPERATOR_COLUMN_NAME)
    private boolean isOperator;
    
    @Column(name = IS_ADMIN_COLUMN_NAME)
    private boolean isAdmin;
    
    public FederationUser() {
        
    }
    
    public FederationUser(String id, String name, String federationId, String fhsId, String email, String description, boolean enabled,
            List<String> attributes, String identityPluginClassName, Map<String, String> identityPluginProperties, 
            boolean isOperator, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.federationId = federationId;
        this.fhsId = fhsId;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
        this.identityPluginClassName = identityPluginClassName;
        this.identityPluginProperties = identityPluginProperties;
        this.isOperator = isOperator;
        this.isAdmin = isAdmin;
    }
    
    public FederationUser(String name, String federationId, String fhsId, String email, String description, 
            boolean enabled, Map<String, String> authenticationProperties, boolean isOperator, boolean isAdmin) {
        this(UUID.randomUUID().toString(), name, federationId, fhsId, email, description, enabled, new ArrayList<String>(),
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

    public String getFhsId() {
        return fhsId;
    }

    public void setFhsId(String fhsId) {
        this.fhsId = fhsId;
    }
}
