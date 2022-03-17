package cloud.fogbow.fhs.api.http.response;

public class FederationId {

    private String name;
    private String id;
    private boolean enabled;

    public FederationId(String name, String id, boolean enabled) {
        this.name = name;
        this.id = id;
        this.enabled = enabled;
    }
    
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
