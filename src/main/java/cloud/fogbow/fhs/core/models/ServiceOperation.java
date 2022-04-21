package cloud.fogbow.fhs.core.models;

import java.util.Objects;

import cloud.fogbow.common.constants.HttpMethod;

public class ServiceOperation {
    private HttpMethod method;
    
    public ServiceOperation(HttpMethod method) {
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceOperation other = (ServiceOperation) obj;
        return method == other.method;
    }
}
