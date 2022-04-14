package cloud.fogbow.fhs.api.http.response;

public class AttributeDescription {
    private String id;
    private String name;
    
    public AttributeDescription(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
