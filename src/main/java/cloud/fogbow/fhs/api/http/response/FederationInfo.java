package cloud.fogbow.fhs.api.http.response;

public class FederationInfo {
    private String federationId;
    private String federationName;
    private Integer nMembers;
    private Integer nServices;
    
    public FederationInfo() {
        
    }
    
    public FederationInfo(String federationId, String federationName, Integer nMembers, Integer nServices) {
        this.federationId = federationId;
        this.federationName = federationName;
        this.nMembers = nMembers;
        this.nServices = nServices;
    }

    public String getFederationId() {
        return federationId;
    }

    public String getFederationName() {
        return federationName;
    }

    public Integer getnMembers() {
        return nMembers;
    }

    public Integer getnServices() {
        return nServices;
    }
}
