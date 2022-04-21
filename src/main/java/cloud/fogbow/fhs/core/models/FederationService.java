package cloud.fogbow.fhs.core.models;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

public class FederationService {
    private String serviceId;
    private String ownerId;
    private String endpoint;
    private ServiceDiscoveryPolicy discoveryPolicy;
    private ServiceAccessPolicy accessPolicy;
    private ServiceInvoker invoker;
    private Map<String, String> metadata;
    
    public FederationService(String serviceId, String ownerId, String endpoint, ServiceDiscoveryPolicy discoveryPolicy,
            ServiceAccessPolicy accessPolicy, ServiceInvoker invoker, Map<String, String> metadata) {
        this.serviceId = serviceId;
        this.ownerId = ownerId;
        this.endpoint = endpoint;
        this.discoveryPolicy = discoveryPolicy;
        this.accessPolicy = accessPolicy;
        this.invoker = invoker;
        this.metadata = metadata;
    }
    
    public FederationService(String ownerId, String endpoint, ServiceDiscoveryPolicy discoveryPolicy,
            ServiceAccessPolicy accessPolicy, ServiceInvoker invoker, Map<String, String> metadata) {
        this(UUID.randomUUID().toString(), ownerId, endpoint, discoveryPolicy, accessPolicy, invoker, metadata);
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

    public ServiceAccessPolicy getAccessPolicy() {
        return accessPolicy;
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
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        return this.invoker.invoke(user, endpoint, method, path, headers, body);
    }
    
    public boolean isDiscoverableBy(FederationUser user) {
        return this.discoveryPolicy.isDiscoverableBy(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, metadata, ownerId, serviceId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FederationService other = (FederationService) obj;
        return Objects.equals(endpoint, other.endpoint) && Objects.equals(metadata, other.metadata)
                && Objects.equals(ownerId, other.ownerId) && Objects.equals(serviceId, other.serviceId);
    }
}
