package cloud.fogbow.fhs.core.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RemoteFederationUserId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    private String fedAdminId;
    
    @Column
    private String fhsId;
    
    public RemoteFederationUserId(String fedAdminId, String fhsId) {
        this.fedAdminId = fedAdminId;
        this.fhsId = fhsId;
    }

    public String getFedAdminId() {
        return fedAdminId;
    }

    public String getFhsId() {
        return fhsId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fedAdminId, fhsId);
    }

    // TODO test
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteFederationUserId other = (RemoteFederationUserId) obj;
        return Objects.equals(fedAdminId, other.fedAdminId) && Objects.equals(fhsId, other.fhsId);
    }
}
