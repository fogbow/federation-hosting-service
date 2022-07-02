package cloud.fogbow.fhs.core.models;

import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationFactory {
    private JsonUtils jsonUtils;
    
    public FederationFactory() {
        this.jsonUtils = new JsonUtils();
    }
    
    public Federation createFederationFactory(String owner, String federationName, Map<String, String> metadata,
            String description, boolean enabled) {
        return new Federation(owner, federationName, metadata, description, enabled);
    }
    
    public Federation createFederation(String federationStr) {
        String[] fields = federationStr.split(Federation.SERIALIZATION_SEPARATOR);
        
        String federationId = fields[0];
        String federationOwner = fields[1];
        String federationName = fields[2];
        String federationFhs = fields[3];
        String federationDescription = fields[4];
        String federationEnabledStr = fields[5];
        String federationMembersStr = fields[6];
        String federationServicesStr = fields[7];
        String federationAttributesStr = fields[8];
        String federationAllowedRemoteJoinsStr = fields[9];
        String federationRemoteAdminsStr = fields[10];
        String federationMetadataStr = fields[11];
        
        Boolean federationEnabled = jsonUtils.fromJson(federationEnabledStr, Boolean.class);
        List<FederationUser> federationMembers = jsonUtils.fromJson(federationMembersStr, 
                new TypeToken<List<FederationUser>>(){});
        List<FederationService> federationServices = jsonUtils.fromJson(federationServicesStr, 
                new TypeToken<List<FederationService>>(){});
        List<FederationAttribute> federationAttributes = jsonUtils.fromJson(federationAttributesStr, 
                new TypeToken<List<FederationAttribute>>(){});
        List<RemoteFederationUser> federationAllowedRemoteJoins = jsonUtils.fromJson(federationAllowedRemoteJoinsStr, 
                new TypeToken<List<RemoteFederationUser>>(){});
        List<FederationUser> federationRemoteAdmins = jsonUtils.fromJson(federationRemoteAdminsStr, 
                new TypeToken<List<FederationUser>>(){});
        Map<String, String> federationMetadata = jsonUtils.fromJson(federationMetadataStr, 
                new TypeToken<Map<String, String>>(){});
        
        return new Federation(federationId, federationOwner, federationName, federationFhs, 
                federationMetadata, federationDescription, federationEnabled, federationMembers, 
                federationAllowedRemoteJoins, federationRemoteAdmins, federationServices, federationAttributes,
                new FederationAuthenticationPluginInstantiator(), new FederationServiceFactory(), jsonUtils);
    }
}
