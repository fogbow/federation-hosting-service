package cloud.fogbow.fhs.core.datastore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

@Component
public class DatabaseManager {
    @Autowired
    private FederationRepository federationRepository;
    
    @Autowired
    private FederationUserRepository federationUserRepository;
    
    @Autowired
    private FederationUpdateRepository federationUpdateRepository;
    
    public DatabaseManager() {
    }
    
    public void saveFederation(Federation federation) {
        this.federationRepository.save(federation);
    }
    
    public void removeFederation(Federation federation) {
        this.federationRepository.delete(federation);
    }
    
    public List<Federation> getFederations() {
        return this.federationRepository.findAll();
    }
    
    public void saveFederationUser(FederationUser federation) {
        this.federationUserRepository.save(federation);
    }
    
    public void removeFederationUser(FederationUser federation) {
        this.federationUserRepository.delete(federation);
    }
    
    public List<FederationUser> getFederationUsers() {
        return this.federationUserRepository.findAll();
    }

    public List<FederationUser> getFederationAdmins() {
        List<FederationUser> federationUsers = getFederationUsers();
        List<FederationUser> federationAdmins = new ArrayList<FederationUser>();

        for (FederationUser federationUser : federationUsers) {
            if (federationUser.isAdmin()) {
                federationAdmins.add(federationUser);
            }
        }
        
        return federationAdmins;
    }
    
    public void saveFederationUpdate(FederationUpdate updatedFederation) {
        this.federationUpdateRepository.save(updatedFederation);
    }
    
    public void removeUpdate(FederationUpdate update) {
        this.federationUpdateRepository.delete(update);
    }
    
    public List<FederationUpdate> getUpdates() {
        return this.federationUpdateRepository.findAll();
    }
}
