package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FederationInfo {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.FEDERATION_ID, notes = ApiDocumentation.Model.FEDERATION_ID_NOTE)
    private String federationId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.FEDERATION_NAME, notes = ApiDocumentation.Model.FEDERATION_NAME_NOTE)
    private String federationName;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.FEDERATION_NUMBER_OF_MEMBERS, notes = ApiDocumentation.Model.FEDERATION_NUMBER_OF_MEMBERS_NOTE)
    private Integer nMembers;
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.FEDERATION_NUMBER_OF_SERVICES, notes = ApiDocumentation.Model.FEDERATION_NUMBER_OF_SERVICES_NOTE)
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
