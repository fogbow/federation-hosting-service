package cloud.fogbow.fhs.api.http.response;

public class FederationDescription {

    private String id;
    private String name;
    private String description;

    public FederationDescription(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
