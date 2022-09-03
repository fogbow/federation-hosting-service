package cloud.fogbow.fhs.api.http.response;

import java.util.List;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FedAdminInfo {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.ADMIN_ID)
    private String memberId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.ADMIN_NAME)
    private String memberName;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.ADMIN_EMAIL)
    private String email;
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.ADMIN_DESCRIPTION)
    private String description;
    @ApiModelProperty(position = 4, example = ApiDocumentation.Model.ADMIN_ENABLED)
    private Boolean enabled;
    @ApiModelProperty(position = 5, example = ApiDocumentation.Model.FEDERATIONS_OWNED_BY_ADMIN, notes = ApiDocumentation.Model.FEDERATIONS_OWNED_BY_ADMIN_NOTE)
    private List<String> fedsOwned;
    
    public FedAdminInfo() {
        
    }
    
    public FedAdminInfo(String memberId, String memberName, String email, String description, Boolean enabled,
            List<String> fedsOwned) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.fedsOwned = fedsOwned;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public List<String> getFedsOwned() {
        return fedsOwned;
    }
}
