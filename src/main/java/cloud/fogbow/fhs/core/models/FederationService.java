package cloud.fogbow.fhs.core.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;

public class FederationService {
    private String serviceId;
    private String ownerId;
    private String endpoint;
    private ServiceDiscoveryPolicy discoveryPolicy;
    private ServiceInvoker invoker;
    private Map<String, String> metadata;
    
    public FederationService(String ownerId, String endpoint, ServiceDiscoveryPolicy discoveryPolicy,
            ServiceInvoker invoker, Map<String, String> metadata) {
        this.serviceId = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.endpoint = endpoint;
        this.discoveryPolicy = discoveryPolicy;
        this.invoker = invoker;
        this.metadata = metadata;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public ServiceDiscoveryPolicy getDiscoveryPolicy() {
        return discoveryPolicy;
    }

    public ServiceInvoker getInvoker() {
        return invoker;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getServiceId() {
        return serviceId;
    }
    
    public ServiceResponse invoke(FederationUser user, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, String> body) throws FogbowException {
        return this.invoker.invoke(user, endpoint, method, path, headers, body);
    }
}
