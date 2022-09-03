package cloud.fogbow.fhs.api.http.response;

import java.util.List;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class FederationMember {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.MEMBER_ID)
    private String memberId;
    @ApiModelProperty(position = 1, example = ApiDocumentation.Model.MEMBER_NAME)
    private String name;
    @ApiModelProperty(position = 2, example = ApiDocumentation.Model.MEMBER_EMAIL)
    private String email;
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.MEMBER_DESCRIPTION)
    private String description;
    @ApiModelProperty(position = 4, example = ApiDocumentation.Model.MEMBER_ENABLED)
    private boolean enabled;
    @ApiModelProperty(position = 5, example = ApiDocumentation.Model.MEMBER_ATTRIBUTES, notes = ApiDocumentation.Model.MEMBER_ATTRIBUTES_NOTE)
    private List<String> attributes;
    
    public FederationMember(String memberId, String name, String email, String description, boolean enabled, 
            List<String> attributes) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.attributes = attributes;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public List<String> getAttributes() {
        return attributes;
    }
}
