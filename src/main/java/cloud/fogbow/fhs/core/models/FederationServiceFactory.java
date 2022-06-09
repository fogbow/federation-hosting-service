package cloud.fogbow.fhs.core.models;

import java.util.Map;

import cloud.fogbow.common.exceptions.InvalidParameterException;

public class FederationServiceFactory {

    public FederationService createService(String ownerId, String endpoint, String discoveryPolicyClassName,
            String accessPolicyClassName, String federationId, Map<String, String> metadata) throws InvalidParameterException {
        return new FederationService(ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, federationId,
                metadata);
    }
}
