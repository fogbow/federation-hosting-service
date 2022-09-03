package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class FederationServiceTest {
    private static final String SERVICE_ID = "serviceId";
    private static final String OWNER_ID = "ownerId";
    private static final String DISCOVERY_POLICY_CLASS_NAME = "discoveryPolicyClassName";
    private static final String ACCESS_POLICY_CLASS_NAME = "accessPolicyClassName";
    private static final String INVOKER_CLASS_NAME = "invokerClassName";
    private static final String NEW_INVOKER_CLASS_NAME = "newInvokerClassName";
    private static final String FEDERATION_ID = "federationId";
    private static final String SERVICE_ENDPOINT = "serviceEndpoint";
    private static final String NEW_DISCOVERY_POLICY_CLASS_NAME = "newDiscoveryPolicyClassName";
    private static final String NEW_ACCESS_POLICY_CLASS_NAME = "newAccessPolicyClassName";
    
    private Map<String, String> metadata;
    private DiscoveryPolicyInstantiator discoveryPolicyInstantiator;
    private AccessPolicyInstantiator accessPolicyInstantiator;
    private ServiceInvokerInstantiator invokerInstantiator;
    private FederationService service;
    private Map<String, String> newMetadata;
    private JsonUtils jsonUtils;
    private String metadataStr;
    private String serviceString;

    @Before
    public void setUp() {
        this.metadata = new HashMap<String, String>();
        this.metadata.put(FederationService.INVOKER_CLASS_NAME_METADATA_KEY, INVOKER_CLASS_NAME);
        
        this.newMetadata = new HashMap<String, String>();
        this.newMetadata.put(FederationService.INVOKER_CLASS_NAME_METADATA_KEY, NEW_INVOKER_CLASS_NAME);
        
        this.discoveryPolicyInstantiator = Mockito.mock(DiscoveryPolicyInstantiator.class);
        this.accessPolicyInstantiator = Mockito.mock(AccessPolicyInstantiator.class);
        this.invokerInstantiator = Mockito.mock(ServiceInvokerInstantiator.class);
        
        this.metadataStr = String.format("{%s:%s}", FederationService.INVOKER_CLASS_NAME_METADATA_KEY, INVOKER_CLASS_NAME);
        this.serviceString = String.join(FederationService.SERIALIZED_FIELDS_SEPARATOR, 
                SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT, DISCOVERY_POLICY_CLASS_NAME, 
                ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, this.metadataStr);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(jsonUtils.toJson(metadata)).thenReturn(metadataStr);
    }
    
    // test case: When calling the constructor, it must instantiate the discovery and access policies
    // and the invoker correctly.
    @Test
    public void testConstructorSetsUpLifeCyclePluginsCorrectly() throws ConfigurationErrorException, InvalidParameterException {
        new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                DISCOVERY_POLICY_CLASS_NAME, ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator, jsonUtils);

        Mockito.verify(this.discoveryPolicyInstantiator).getDiscoveryPolicy(DISCOVERY_POLICY_CLASS_NAME);
        Mockito.verify(this.accessPolicyInstantiator).getAccessPolicy(ACCESS_POLICY_CLASS_NAME, metadata);
        Mockito.verify(this.invokerInstantiator).getInvoker(INVOKER_CLASS_NAME, metadata, FEDERATION_ID);
    }
    
    // test case: When calling the constructor which receives a String, it must load the 
    // service data from the String correctly. 
    @Test
    public void testConstructorInstantiatesServiceFromStringCorrectly() {
        FederationService service = new FederationService(serviceString);
        
        assertEquals(SERVICE_ID, service.getServiceId());
        assertEquals(OWNER_ID, service.getOwnerId());
        assertEquals(SERVICE_ENDPOINT, service.getEndpoint());
        assertEquals(DISCOVERY_POLICY_CLASS_NAME, service.getDiscoveryPolicyClassName());
        assertEquals(ACCESS_POLICY_CLASS_NAME, service.getAccessPolicyClassName());
        assertEquals(INVOKER_CLASS_NAME, service.getInvokerClassName());
        assertEquals(FEDERATION_ID, service.getFederationId());
        assertEquals(this.metadata, service.getMetadata());
    }
    
    // test case: When calling the update method, it must instantiate the discovery and access policies
    // and the invoker using the new data passed as argument.
    @Test
    public void testUpdate() throws InvalidParameterException {
        this.service = new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                DISCOVERY_POLICY_CLASS_NAME, ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator, jsonUtils);
        
        this.service.update(newMetadata, NEW_DISCOVERY_POLICY_CLASS_NAME, NEW_ACCESS_POLICY_CLASS_NAME);
        
        Mockito.verify(this.discoveryPolicyInstantiator).getDiscoveryPolicy(NEW_DISCOVERY_POLICY_CLASS_NAME);
        Mockito.verify(this.accessPolicyInstantiator).getAccessPolicy(NEW_ACCESS_POLICY_CLASS_NAME, newMetadata);
        Mockito.verify(this.invokerInstantiator).getInvoker(NEW_INVOKER_CLASS_NAME, newMetadata, FEDERATION_ID);
    }
    
    // test case: When calling the serialize method, it must return a String in specific format containing
    // the FederationService data.
    @Test
    public void testSerialize() throws InvalidParameterException {
        this.service = new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                DISCOVERY_POLICY_CLASS_NAME, ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator, jsonUtils);
        
        String serializedService = this.service.serialize();

        String expectedString = String.join(FederationService.SERIALIZED_FIELDS_SEPARATOR, 
                SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT, DISCOVERY_POLICY_CLASS_NAME, 
                ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadataStr);
        
        assertEquals(expectedString, serializedService);
    }
    
    // test case: When calling the serialize method and the discoveryPolicyClassName is null, it must
    // use a default value for the discoveryPolicyClassName field in the result String.
    @Test
    public void testSerializeWithNullDiscoveryPolicyClassName() throws InvalidParameterException {
        this.service = new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                null, ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator, jsonUtils);
        
        String serializedService = this.service.serialize();

        String expectedString = String.join(FederationService.SERIALIZED_FIELDS_SEPARATOR, 
                SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT, FederationService.EMPTY_DISCOVERY_POLICY_CLASS_NAME, 
                ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadataStr);
        
        assertEquals(expectedString, serializedService);
    }
    
    // test case: When calling the serialize method and the discoveryPolicyClassName is empty, it must
    // use a default value for the discoveryPolicyClassName field in the result String.
    @Test
    public void testSerializeWithEmptyDiscoveryPolicyClassName() throws InvalidParameterException {
        this.service = new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                "", ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator, jsonUtils);
        
        String serializedService = this.service.serialize();

        String expectedString = String.join(FederationService.SERIALIZED_FIELDS_SEPARATOR, 
                SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT, FederationService.EMPTY_DISCOVERY_POLICY_CLASS_NAME, 
                ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadataStr);
        
        assertEquals(expectedString, serializedService);
    }
}
