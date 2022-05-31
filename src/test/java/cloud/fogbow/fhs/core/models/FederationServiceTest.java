package cloud.fogbow.fhs.core.models;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;

public class FederationServiceTest {
    private static final String SERVICE_ID = "serviceId";
    private static final String OWNER_ID = "ownerId";
    private static final String DISCOVERY_POLICY_CLASS_NAME = "discoveryPolicyClassName";
    private static final String ACCESS_POLICY_CLASS_NAME = "accessPolicyClassName";
    private static final String INVOKE_CLASS_NAME = "invokerClassName";
    private static final String FEDERATION_ID = "federationId";
    private static final String SERVICE_ENDPOINT = "serviceEndpoint";
    
    private Map<String, String> metadata;
    private DiscoveryPolicyInstantiator discoveryPolicyInstantiator;
    private AccessPolicyInstantiator accessPolicyInstantiator;
    private ServiceInvokerInstantiator invokerInstantiator;

    @Before
    public void setUp() {
        this.metadata = new HashMap<String, String>();
        this.metadata.put(FederationService.INVOKER_CLASS_NAME_METADATA_KEY, INVOKE_CLASS_NAME);
        
        this.discoveryPolicyInstantiator = Mockito.mock(DiscoveryPolicyInstantiator.class);
        this.accessPolicyInstantiator = Mockito.mock(AccessPolicyInstantiator.class);
        this.invokerInstantiator = Mockito.mock(ServiceInvokerInstantiator.class);
    }
    
    @Test
    public void testConstructorSetsUpLifeCyclePluginsCorrectly() throws ConfigurationErrorException {
        new FederationService(SERVICE_ID, OWNER_ID, SERVICE_ENDPOINT,
                DISCOVERY_POLICY_CLASS_NAME, ACCESS_POLICY_CLASS_NAME, FEDERATION_ID, metadata, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, invokerInstantiator);

        Mockito.verify(this.discoveryPolicyInstantiator).getDiscoveryPolicy(DISCOVERY_POLICY_CLASS_NAME);
        Mockito.verify(this.accessPolicyInstantiator).getAccessPolicy(ACCESS_POLICY_CLASS_NAME, metadata);
        Mockito.verify(this.invokerInstantiator).getInvoker(INVOKE_CLASS_NAME, metadata, FEDERATION_ID);
    }
}
