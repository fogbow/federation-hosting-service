package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class MemberId {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.MEMBER_ID)
    private String memberId;
    
    public MemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
}
