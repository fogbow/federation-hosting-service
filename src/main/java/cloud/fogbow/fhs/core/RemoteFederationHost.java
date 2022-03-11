package cloud.fogbow.fhs.core;

import java.util.List;

import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationService;

public class RemoteFederationHost {
    public List<FederationService> getAuthorizedServices(String federationId, String memberId) {
        return null;
    }
    
    public List<Federation> getRemoteFederations() {
        return null;
    }
    
    public void joinRemoteFederation(String federationId) {
        
    }
    
    public void leaveRemoteFederation(String federationId) {
        
    }
    
    public void updateRemoteFederation(Federation federation) {
        
    }
}
