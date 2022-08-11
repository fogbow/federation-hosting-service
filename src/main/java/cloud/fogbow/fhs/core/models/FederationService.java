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
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.JsonUtils;

@Entity
@Table(name = "federation_service_table")
public class FederationService {
    private static final String SERVICE_ID_COLUMN_NAME = "service_id";
    private static final String SERVICE_OWNER_ID_COLUMN_NAME = "service_owner_id";
    private static final String SERVICE_ENDPOINT_COLUMN_NAME = "service_endpoint";
    private static final String DISCOVERY_POLICY_CLASS_NAME_COLUMN_NAME = "discovery_policy_class_name";
    private static final String ACCESS_POLICY_CLASS_NAME_COLUMN_NAME = "access_policy_class_name";
    private static final String INVOKER_CLASS_NAME_COLUMN_NAME = "invoker_class_name";
    private static final String FEDERATION_ID_COLUMN_NAME = "federation_id";
    private static final String METADATA_COLUMN_NAME = "metadata";
    
    // TODO documentation
    public static final String INVOKER_CLASS_NAME_METADATA_KEY = "invokerClassName";
    public static final String CREDENTIALS_METADATA_KEY = "credentials";
    
    @Column(name = SERVICE_ID_COLUMN_NAME)
    @Id
    private String serviceId;
    
    @Column(name = SERVICE_OWNER_ID_COLUMN_NAME)
    private String ownerId;
    
    @Column(name = SERVICE_ENDPOINT_COLUMN_NAME)
    private String endpoint;

    @Column(name = DISCOVERY_POLICY_CLASS_NAME_COLUMN_NAME)
    private String discoveryPolicyClassName;
    
    @Column(name = ACCESS_POLICY_CLASS_NAME_COLUMN_NAME)
    private String accessPolicyClassName;
    
    @Column(name = INVOKER_CLASS_NAME_COLUMN_NAME)
    private String invokerClassName;
    
    @Column(name = FEDERATION_ID_COLUMN_NAME)
    private String federationId;
    
    @Column(name = METADATA_COLUMN_NAME, columnDefinition="text", length=10485760)
    @ElementCollection
    private Map<String, String> metadata;
    
    @Transient
    private ServiceDiscoveryPolicy discoveryPolicy;
    
    @Transient
    private ServiceAccessPolicy accessPolicy;
    
    @Transient
    private ServiceInvoker invoker;
    
    @PostLoad
    private void startUp() {
        setUpServiceLifeCyclePlugins(new DiscoveryPolicyInstantiator(), new AccessPolicyInstantiator(), 
                new ServiceInvokerInstantiator());
    }
    
    private void setUpServiceLifeCyclePlugins(DiscoveryPolicyInstantiator discoveryPolicyInstantiator, 
            AccessPolicyInstantiator accessPolicyInstantiator, ServiceInvokerInstantiator invokerInstantiator) {
        this.discoveryPolicy = discoveryPolicyInstantiator.getDiscoveryPolicy(this.discoveryPolicyClassName);
        this.accessPolicy = accessPolicyInstantiator.getAccessPolicy(this.accessPolicyClassName, this.metadata);
        this.invoker = invokerInstantiator.getInvoker(this.invokerClassName, this.metadata, this.federationId);
    }
    
    public FederationService() {
        
    }
    
    public FederationService(String serviceId, String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, String federationId, Map<String, String> metadata, 
            DiscoveryPolicyInstantiator discoveryPolicyInstantiator, AccessPolicyInstantiator accessPolicyInstantiator, 
            ServiceInvokerInstantiator invokerInstantiator) throws InvalidParameterException {
        if (endpoint == null || endpoint.isEmpty()) {
            throw new InvalidParameterException(
                    Messages.Exception.SERVICE_ENDPOINT_CANNOT_BE_NULL_OR_EMPTY);
        }
        
        this.serviceId = serviceId;
        this.ownerId = ownerId;
        this.endpoint = endpoint;
        this.metadata = metadata;
        this.federationId = federationId;
        this.discoveryPolicyClassName = discoveryPolicyClassName;
        this.accessPolicyClassName = accessPolicyClassName;
        this.invokerClassName = this.metadata.get(INVOKER_CLASS_NAME_METADATA_KEY);
        
        setUpServiceLifeCyclePlugins(discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator);
    }
    
    public FederationService(String ownerId, String endpoint, String discoveryPolicyClassName, 
            String accessPolicyClassName, String federationId, Map<String, String> metadata) throws InvalidParameterException {
        this(UUID.randomUUID().toString(), ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, 
                federationId, metadata, new DiscoveryPolicyInstantiator(), new AccessPolicyInstantiator(), 
                new ServiceInvokerInstantiator());
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

    // TODO test
    public void update(Map<String, String> metadata, String discoveryPolicyClassName, String accessPolicyClassName) {
        String invokerClassName = metadata.get(INVOKER_CLASS_NAME_METADATA_KEY);
        
        this.discoveryPolicyClassName = discoveryPolicyClassName;
        this.accessPolicyClassName = accessPolicyClassName;
        this.invokerClassName = invokerClassName;
        this.metadata = metadata;
        
        setUpServiceLifeCyclePlugins(new DiscoveryPolicyInstantiator(), new AccessPolicyInstantiator(), 
                new ServiceInvokerInstantiator());
    }
    
    // TODO test
    public String serialize() {
        if (this.discoveryPolicyClassName == null || this.discoveryPolicyClassName.isEmpty()) {
            // FIXME constant
            return String.join("!^!", this.serviceId, this.ownerId, this.endpoint, "null",
                    this.accessPolicyClassName, this.federationId, new JsonUtils().toJson(this.metadata));
        } else {
            // FIXME constant
            return String.join("!^!", this.serviceId, this.ownerId, this.endpoint, this.discoveryPolicyClassName,
                    this.accessPolicyClassName, this.federationId, new JsonUtils().toJson(this.metadata));
        }
    }
}
