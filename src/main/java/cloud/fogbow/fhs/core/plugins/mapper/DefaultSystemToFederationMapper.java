package cloud.fogbow.fhs.core.plugins.mapper;

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
