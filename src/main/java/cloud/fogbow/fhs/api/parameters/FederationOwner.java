package cloud.fogbow.fhs.api.parameters;

public class FederationOwner {
    private String owner;
    
    public FederationOwner() {
        
    }
    
    public FederationOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
