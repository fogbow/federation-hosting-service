package cloud.fogbow.fhs.core.plugins.access;

import java.util.List;
import java.util.Objects;

import cloud.fogbow.fhs.core.models.ServiceOperation;

public class AccessLevel {
    private String name;
    private List<ServiceOperation> allowedOperations;

    public AccessLevel(String name, List<ServiceOperation> allowedOperations) {
        this.name = name;
        this.allowedOperations = allowedOperations;
    }

    public String getName() {
        return name;
    }
    
    public List<ServiceOperation> getAllowedOperations() {
        return allowedOperations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedOperations, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccessLevel other = (AccessLevel) obj;
        return Objects.equals(allowedOperations, other.allowedOperations) && Objects.equals(name, other.name);
    }
}
