package cloud.fogbow.fhs.core.models;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "remote_federation_user_table")
public class RemoteFederationUser {
    @Id
    @Embedded
    private RemoteFederationUserId id;
    
    public RemoteFederationUser(String fedAdminId, String fhsId) {
        this.id = new RemoteFederationUserId(fedAdminId, fhsId);
    }

    public String getFedAdminId() {
        return this.id.getFedAdminId();
    }

    public String getFhsId() {
        return this.id.getFhsId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
        RemoteFederationUser other = (RemoteFederationUser) obj;
        return Objects.equals(id, other.id);
    }
}
