package cloud.fogbow.fhs.core.datastore;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.fogbow.fhs.core.models.Federation;

@Repository
@Transactional
public interface FederationRepository extends JpaRepository<Federation, String>{

}
