package cloud.fogbow.fhs.core.models;

import java.util.UUID;

public class FederationAttribute {
    private String id;
    private String name;
    
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
