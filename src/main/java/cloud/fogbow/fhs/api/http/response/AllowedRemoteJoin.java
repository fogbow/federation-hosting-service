package cloud.fogbow.fhs.api.http.response;

public class AllowedRemoteJoin {
    private String federationId;
    private String federationName;
    private String remoteFedAdminId;
    private String fhsId;

    public AllowedRemoteJoin() {
        
    }
    
    public AllowedRemoteJoin(String federationId, String federationName, String remoteFedAdminId, String fhsId) {
        this.federationId = federationId;
        this.federationName = federationName;
        this.remoteFedAdminId = remoteFedAdminId;
        this.fhsId = fhsId;
    }

    public String getFederationId() {
        return federationId;
    }

    public String getFederationName() {
        return federationName;
    }

    public String getRemoteFedAdminId() {
        return remoteFedAdminId;
    }

    public String getFhsId() {
        return fhsId;
    }
}
