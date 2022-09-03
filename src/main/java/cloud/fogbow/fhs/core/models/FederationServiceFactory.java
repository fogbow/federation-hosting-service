package cloud.fogbow.fhs.core.models;

import java.util.Map;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationServiceFactory {

    public FederationService createService(String ownerId, String endpoint, String discoveryPolicyClassName,
            String accessPolicyClassName, String federationId, Map<String, String> metadata) throws InvalidParameterException {
        return new FederationService(ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, federationId,
                metadata);
    }
    
    public FederationService deserialize(String str) throws InvalidParameterException {
        return new FederationService(str, new DiscoveryPolicyInstantiator(), 
                new AccessPolicyInstantiator(), new ServiceInvokerInstantiator(), new JsonUtils());
    }
}
