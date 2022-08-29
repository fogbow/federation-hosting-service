package cloud.fogbow.fhs.api.parameters;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class RemoteMembership {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.ADMIN_ID, notes = ApiDocumentation.Model.ADMIN_ID_NOTE)
    private String fedAdminId;
    @ApiModelProperty(position = 1, required = true, example = ApiDocumentation.Model.FHS_ID, notes = ApiDocumentation.Model.FHS_ID_NOTE)
    private String fhsId;
    @ApiModelProperty(position = 2, required = true, example = ApiDocumentation.Model.FEDERATION_ID, notes = ApiDocumentation.Model.FEDERATION_ID_NOTE)
    private String federationId;

    public String getFedAdminId() {
        return fedAdminId;
    }

    public String getFhsId() {
        return fhsId;
    }

    public String getFederationId() {
        return federationId;
    }
}
