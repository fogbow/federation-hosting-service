package cloud.fogbow.fhs.core.plugins.invocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.fhs.core.FhsClassFactory;
import cloud.fogbow.fhs.core.utils.MapUtils;

// TODO documentation
public class ServiceInvokerInstantiatorTest {
    private static final String INVOKER_CLASS_NAME = "className";
    private static final String FEDERATION_ID = "federationId";
    private static final String METADATA_KEY_1 = "metadataKey1";
    private static final String METADATA_KEY_2 = "metadataKey2";
    private static final String METADATA_VALUE_1 = "metadataValue1";
    private static final String METADATA_VALUE_2 = "metadataValue2";
    private static final String SERIALIZED_METADATA = "serializedMetadata";
    private ServiceInvokerInstantiator instantiator;
    private FhsClassFactory classFactory;
    private Map<String, String> metadata;
    private MapUtils mapUtils;
    private ServiceInvoker invoker;
    
    @Before
    public void setUp() {
        this.metadata = new HashMap<String, String>();
        this.metadata.put(METADATA_KEY_1, METADATA_VALUE_1);
        this.metadata.put(METADATA_KEY_2, METADATA_VALUE_2);
        
        Map<String, String> expectedMetadata = new HashMap<String, String>();
        expectedMetadata.put(METADATA_KEY_1, METADATA_VALUE_1);
        expectedMetadata.put(METADATA_KEY_2, METADATA_VALUE_2);
        expectedMetadata.put(ServiceInvokerInstantiator.FEDERATION_ID_KEY, FEDERATION_ID);
        
        this.invoker = Mockito.mock(ServiceInvoker.class);
        
        this.classFactory = Mockito.mock(FhsClassFactory.class);
        Mockito.when(this.classFactory.createPluginInstance(
                Mockito.anyString(), Mockito.anyString())).thenReturn(invoker);
        
        this.mapUtils = Mockito.mock(MapUtils.class);
        Mockito.when(this.mapUtils.serializeMap(expectedMetadata)).thenReturn(SERIALIZED_METADATA);
        
        instantiator = new ServiceInvokerInstantiator(classFactory, mapUtils);
    }
    
    @Test
    public void testGetInvoker() {
        ServiceInvoker returnedInvoker = instantiator.getInvoker(INVOKER_CLASS_NAME, metadata, FEDERATION_ID);
        
        assertEquals(invoker, returnedInvoker);
        Mockito.verify(this.classFactory).createPluginInstance(INVOKER_CLASS_NAME, SERIALIZED_METADATA);
    }
    
    @Test
    public void testGetInvokerWithEmptyClassName() {
        ServiceInvoker returnedInvoker = instantiator.getInvoker("", metadata, FEDERATION_ID);
        
        assertNotNull(returnedInvoker);
    }
    
    @Test
    public void testGetInvokerWithNullClassName() {
        ServiceInvoker returnedInvoker = instantiator.getInvoker(null, metadata, FEDERATION_ID);
        
        assertNotNull(returnedInvoker);
    }
}
