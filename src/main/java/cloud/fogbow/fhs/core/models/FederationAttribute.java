package cloud.fogbow.fhs.core.models;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// FIXME constant
@Entity
@Table(name = "federation_attribute_table")
public class FederationAttribute {
    
    // FIXME constant
    @Column(name = "federation_attribute_id")
    @Id
    private String id;
    
    // FIXME constant
    @Column(name = "federation_attribute_name")
    private String name;
    
    public FederationAttribute() {
        
    }
    
    public FederationAttribute(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public FederationAttribute(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
