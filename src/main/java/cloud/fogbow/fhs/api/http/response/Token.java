package cloud.fogbow.fhs.api.http.response;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class Token {
    @ApiModelProperty(position = 0, example = ApiDocumentation.Model.TOKEN, notes = ApiDocumentation.Model.TOKEN_NOTE)
    private String token;
    
    public Token(String encryptedToken) {
        this.token = encryptedToken;
    }

    public String getToken() {
        return token;
    }
}
