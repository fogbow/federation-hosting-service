package cloud.fogbow.fhs.core.models;

import cloud.fogbow.common.models.FogbowOperation;

public class FhsOperation extends FogbowOperation {

    private OperationType operationType;

    public FhsOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}
