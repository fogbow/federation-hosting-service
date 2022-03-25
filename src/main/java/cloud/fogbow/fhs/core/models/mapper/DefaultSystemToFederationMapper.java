package cloud.fogbow.fhs.core.models.mapper;

import cloud.fogbow.fhs.core.models.SystemToFederationMapper;

public class DefaultSystemToFederationMapper implements SystemToFederationMapper {

    private String federationId;
    
    public DefaultSystemToFederationMapper(String federationId) {
        this.federationId = federationId;
    }
    
    @Override
    public String systemIdToFederationId(String systemId) {
        return String.format("%s@%s", systemId, this.federationId);
    }
}
