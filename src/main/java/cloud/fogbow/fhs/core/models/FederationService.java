package cloud.fogbow.fhs.core.models;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

@Entity
@Table(name = "federation_service_table")
public class FederationService {
    public static final String INVOKER_CLASS_NAME_METADATA_KEY = "invokerClassName";
    public static final String CREDENTIALS_METADATA_KEY = "credentials";
    
    @Column(name = "service_id")
    @Id
    private String serviceId;
    
    @Column(name = "service_owner_id")
    private String ownerId;
    
    @Column(name = "service_endpoint")
    private String endpoint;

    @Column(name = "discovery_policy_class_name")
    private String discoveryPolicyClassName;
    
    @Column(name = "access_policy_class_name")
    private String accessPolicyClassName;
    
    @Column(name = "federation_id")
    private String federationId;
    
    @Transient
    private ServiceDiscoveryPolicy discoveryPolicy;
    
    @Transient
    private ServiceAccessPolicy accessPolicy;
    
    @Transient
    private ServiceInvoker invoker;
    
    @Column(name = "metadata", columnDefinition="text", length=10485760)
    @ElementCollection
    private Map<String, String> metadata;
    
    @PostLoad
    private void startUp() {
        this.discoveryPolicy = new DiscoveryPolicyInstantiator().getDiscoveryPolicy(discoveryPolicyClassName);
        this.accessPolicy = new AccessPolicyInstantiator().getAccessPolicy(accessPolicyClassName, metadata);
        
        String invokerClassName = metadata.get(INVOKER_CLASS_NAME_METADATA_KEY);
        this.invoker = new ServiceInvokerInstantiator().getInvoker(invokerClassName, metadata, federationId);
    }
    
    public FederationService() {
        
    }
    
    public FederationService(String serviceId, String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, String federationId, Map<String, String> metadata) {
        this.serviceId = serviceId;
        this.ownerId = ownerId;
        this.endpoint = endpoint;
        this.metadata = metadata;

        this.discoveryPolicyClassName = discoveryPolicyClassName;
        this.discoveryPolicy = new DiscoveryPolicyInstantiator().getDiscoveryPolicy(discoveryPolicyClassName);
        
        this.accessPolicyClassName = accessPolicyClassName;
        this.accessPolicy = new AccessPolicyInstantiator().getAccessPolicy(accessPolicyClassName, metadata);
        
        String invokerClassName = this.metadata.get(INVOKER_CLASS_NAME_METADATA_KEY);
        this.invoker = new ServiceInvokerInstantiator().getInvoker(invokerClassName, this.metadata, federationId);
    }
    
    public FederationService(String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, String federationId, Map<String, String> metadata) {
        this(UUID.randomUUID().toString(), ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, 
                federationId, metadata);
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
    
    public void setDiscoveryPolicy(ServiceDiscoveryPolicy discoveryPolicy) {
        this.discoveryPolicy = discoveryPolicy;
    }

    public ServiceAccessPolicy getAccessPolicy() {
        return accessPolicy;
    }
    
    public void setAccessPolicy(ServiceAccessPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }
    
    public ServiceInvoker getInvoker() {
        return invoker;
    }
    
    public void setInvoker(ServiceInvoker invoker) {
        this.invoker = invoker;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getServiceId() {
        return serviceId;
    }
    
    public ServiceResponse invoke(FederationUser user, HttpMethod method, 
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        return this.invoker.invoke(user, serviceId, endpoint, method, path, headers, body);
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
