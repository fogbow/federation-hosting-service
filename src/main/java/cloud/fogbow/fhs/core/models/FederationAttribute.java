package cloud.fogbow.fhs.core.models;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "federation_attribute_table")
public class FederationAttribute {
    private static final String FEDERATION_ATTRIBUTE_ID_COLUMN_NAME = "federation_attribute_id";
    private static final String FEDERATION_ATTRIBUTE_NAME_COLUMN_NAME = "federation_attribute_name";

    @Column(name = FEDERATION_ATTRIBUTE_ID_COLUMN_NAME)
    @Id
    private String id;
    
    @Column(name = FEDERATION_ATTRIBUTE_NAME_COLUMN_NAME)
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
