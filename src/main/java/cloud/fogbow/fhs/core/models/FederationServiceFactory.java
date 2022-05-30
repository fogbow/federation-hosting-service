package cloud.fogbow.fhs.core.models;

import java.util.Map;

public class FederationServiceFactory {

    public FederationService createService(String ownerId, String endpoint, String discoveryPolicyClassName,
            String accessPolicyClassName, String federationId, Map<String, String> metadata) {
        return new FederationService(ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, federationId,
                metadata);
    }
}
