package cloud.fogbow.fhs;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FederationUser;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class MainTest {
    private static final String OPERATOR_ID_1 = "operator1";
    private static final String OPERATOR_ID_2 = "operator2";
    private static final String OPERATOR_ID_3 = "operator3";
    private static final String OPERATOR_IDS = 
            String.join(SystemConstants.OPERATOR_IDS_SEPARATOR, OPERATOR_ID_1, OPERATOR_ID_2, OPERATOR_ID_3);
    private static final String PROPERTY_1 = "identityPluginClassName";
    private static final String PROPERTY_2 = "property2";
    private static final String PROPERTY_3 = "property3";
    private static final String OPERATOR_1_PROPERTY_1_KEY = OPERATOR_ID_1 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_1_PROPERTY_2_KEY = OPERATOR_ID_1 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_1_PROPERTY_3_KEY = OPERATOR_ID_1 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String OPERATOR_2_PROPERTY_1_KEY = OPERATOR_ID_2 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_2_PROPERTY_2_KEY = OPERATOR_ID_2 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_2_PROPERTY_3_KEY = OPERATOR_ID_2 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String OPERATOR_3_PROPERTY_1_KEY = OPERATOR_ID_3 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_3_PROPERTY_2_KEY = OPERATOR_ID_3 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_3_PROPERTY_3_KEY = OPERATOR_ID_3 + Main.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String NOT_OPERATOR_PROPERTY_KEY = "not_operator_property_key";
    private static final String OPERATOR_1_PROPERTY_1_VALUE = "operator1_property1";
    private static final String OPERATOR_1_PROPERTY_2_VALUE = "operator1_property2";
    private static final String OPERATOR_1_PROPERTY_3_VALUE = "operator1_property3";
    private static final String OPERATOR_2_PROPERTY_1_VALUE = "operator2_property1";
    private static final String OPERATOR_2_PROPERTY_2_VALUE = "operator2_property2";
    private static final String OPERATOR_2_PROPERTY_3_VALUE = "operator2_property3";
    private static final String OPERATOR_3_PROPERTY_1_VALUE = "operator3_property1";
    private static final String OPERATOR_3_PROPERTY_2_VALUE = "operator3_property2";
    private static final String OPERATOR_3_PROPERTY_3_VALUE = "operator3_property3";
    private static final String NOT_OPERATOR_PROPERTY_VALUE = "not_operator_property_value";
    private PropertiesHolder propertiesHolder;
    private Properties properties;
    private Main main;
    
    @Before
    public void setUp() {
        HashSet<Object> propertiesKeySet = new HashSet<Object>();
        propertiesKeySet.add(OPERATOR_1_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_1_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_1_PROPERTY_3_KEY);
        propertiesKeySet.add(NOT_OPERATOR_PROPERTY_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_3_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_3_KEY);
        
        this.properties = Mockito.mock(Properties.class);
        Mockito.when(this.properties.keySet()).thenReturn(propertiesKeySet);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_1_KEY)).thenReturn(OPERATOR_1_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_2_KEY)).thenReturn(OPERATOR_1_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_3_KEY)).thenReturn(OPERATOR_1_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_1_KEY)).thenReturn(OPERATOR_2_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_2_KEY)).thenReturn(OPERATOR_2_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_3_KEY)).thenReturn(OPERATOR_2_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_1_KEY)).thenReturn(OPERATOR_3_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_2_KEY)).thenReturn(OPERATOR_3_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_3_KEY)).thenReturn(OPERATOR_3_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(NOT_OPERATOR_PROPERTY_KEY)).thenReturn(NOT_OPERATOR_PROPERTY_VALUE);
        
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(OPERATOR_IDS);
        Mockito.when(this.propertiesHolder.getProperties()).thenReturn(properties);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.main = new Main();
    }
    
    @Test
    public void testLoadFhsOperators() throws ConfigurationErrorException {
        List<FederationUser> fhsOperators = main.loadFhsOperatorsOrFail();
        
        assertEquals(3, fhsOperators.size());
        
        FederationUser operator1 = fhsOperators.get(0);
        Map<String, String> operator1Properties = operator1.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_1, operator1.getName());
        assertEquals(OPERATOR_1_PROPERTY_1_VALUE, operator1Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_1_PROPERTY_2_VALUE, operator1Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_1_PROPERTY_3_VALUE, operator1Properties.get(PROPERTY_3));
        
        FederationUser operator2 = fhsOperators.get(1);
        Map<String, String> operator2Properties = operator2.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_2, operator2.getName());
        assertEquals(OPERATOR_2_PROPERTY_1_VALUE, operator2Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_2_PROPERTY_2_VALUE, operator2Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_2_PROPERTY_3_VALUE, operator2Properties.get(PROPERTY_3));
        
        FederationUser operator3 = fhsOperators.get(2);
        Map<String, String> operator3Properties = operator3.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_3, operator3.getName());
        assertEquals(OPERATOR_3_PROPERTY_1_VALUE, operator3Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_3_PROPERTY_2_VALUE, operator3Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_3_PROPERTY_3_VALUE, operator3Properties.get(PROPERTY_3));
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testLoadFhsOperatorsEmptyOperatorsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn("");
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        main.loadFhsOperatorsOrFail();
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testLoadFhsOperatorsNullOperatorsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(null);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        main.loadFhsOperatorsOrFail();
    }
}
