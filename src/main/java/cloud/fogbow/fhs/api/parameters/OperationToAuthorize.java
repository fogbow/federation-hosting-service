package cloud.fogbow.fhs.api.parameters;

public class OperationToAuthorize {
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
