package cloud.fogbow.fhs.api.http.response;

public class Authorized {
    private boolean authorized;
    
    public Authorized() {
        
    }
    
    public Authorized(boolean isAuthorized) {
        this.authorized = isAuthorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
