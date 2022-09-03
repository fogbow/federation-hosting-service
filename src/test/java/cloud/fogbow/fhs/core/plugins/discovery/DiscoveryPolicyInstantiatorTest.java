package cloud.fogbow.fhs.core.plugins.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.fhs.core.FhsClassFactory;

public class DiscoveryPolicyInstantiatorTest {
    private static final String POLICY_CLASS_NAME = "className";
    private static final String ARG_0 = "arg0";
    private static final String ARG_1 = "arg1";
    private DiscoveryPolicyInstantiator instantiator;
    private FhsClassFactory classFactory;
    private ServiceDiscoveryPolicy discoveryPolicy;
    
    @Before
    public void setUp() {
        this.discoveryPolicy = Mockito.mock(ServiceDiscoveryPolicy.class);
        
        this.classFactory = Mockito.mock(FhsClassFactory.class);
        Mockito.when(this.classFactory.createPluginInstance(
                POLICY_CLASS_NAME, ARG_0, ARG_1)).thenReturn(this.discoveryPolicy);
        
        this.instantiator = new DiscoveryPolicyInstantiator(this.classFactory);
    }
    
    @Test
    public void testGetDiscoveryPolicy() {
        ServiceDiscoveryPolicy returnedPolicy = instantiator.getDiscoveryPolicy(POLICY_CLASS_NAME, ARG_0, ARG_1);
        
        assertEquals(this.discoveryPolicy, returnedPolicy);
    }

    @Test
    public void testGetDiscoveryPolicyWithEmptyClassName() {    
        ServiceDiscoveryPolicy returnedPolicy = instantiator.getDiscoveryPolicy("", ARG_0, ARG_1);
        
        assertNotNull(returnedPolicy);
    }
    
    @Test
    public void testGetDiscoveryPolicyWithNullClassName() {
        ServiceDiscoveryPolicy returnedPolicy = instantiator.getDiscoveryPolicy(null, ARG_0, ARG_1);
        
        assertNotNull(returnedPolicy);
    }
}
