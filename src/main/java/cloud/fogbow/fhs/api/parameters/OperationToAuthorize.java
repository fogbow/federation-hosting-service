package cloud.fogbow.fhs.api.parameters;

import cloud.fogbow.fhs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class OperationToAuthorize {
    @ApiModelProperty(position = 0, required = true, example = ApiDocumentation.Model.OPERATION_STR, notes = ApiDocumentation.Model.OPERATION_STR_NOTE)
    private String operation;
    
    public OperationToAuthorize() {
        
    }

    public OperationToAuthorize(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
