package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class AllowedRemoteJoin {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.FEDERATION_ID, notes = ApiDocumentation.Model.FEDERATION_ID_NOTE)
    private String federationId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.FEDERATION_NAME, notes = ApiDocumentation.Model.FEDERATION_NAME_NOTE)
    private String federationName;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.ADMIN_ID, notes = ApiDocumentation.Model.ADMIN_ID_NOTE)
    private String remoteFedAdminId;
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.FHS_ID, notes = ApiDocumentation.Model.FHS_ID_NOTE)
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
