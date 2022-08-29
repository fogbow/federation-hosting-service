package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class Authorized {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.AUTHORIZED, notes = ApiDocumentation.Model.AUTHORIZED_NOTE)
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
