package cloud.fogbow.fhs.core.intercomponent.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class TimeBasedSynchronizationMechanismTest {
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String FHS_ID_3 = "fhsId3";
    private static final String ALLOWED_FHS_IDS_STR = 
            String.join(SystemConstants.ALLOWED_FHS_IDS_SEPARATOR, 
                    FHS_ID_1, FHS_ID_2, FHS_ID_3);
    private static final long SLEEP_TIME = 1L;
    private static final String LOCAL_FHS_ID = "localFhsId";

    private TimeBasedSynchronizationMechanism syncMechanism;
    private FederationHost federationHost;
    private PropertiesHolder propertiesHolder;
    
    @Before
    public void setUp() {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(ALLOWED_FHS_IDS_STR);
        Mockito.when(this.propertiesHolder.getProperty
                (ConfigurationPropertyKeys.SYNCHRONIZATION_SLEEP_TIME)).thenReturn(String.valueOf(SLEEP_TIME));
        Mockito.when(this.propertiesHolder.getProperty
                (ConfigurationPropertyKeys.PROVIDER_ID_KEY)).thenReturn(String.valueOf(LOCAL_FHS_ID));
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.federationHost = Mockito.mock(FederationHost.class);
    }
    
    @Test
    public void testConstructorReadsPropertiesCorrectly() throws ConfigurationErrorException {
        this.syncMechanism = new TimeBasedSynchronizationMechanism(this.federationHost);
        
        assertEquals(3, this.syncMechanism.getAllowedFhssIds().size());
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_1));
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_2));
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_3));
        assertEquals(SLEEP_TIME, this.syncMechanism.getSleepTime());
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidAllowedFhsIdsProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.federationHost);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidSleepTimeProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.SYNCHRONIZATION_SLEEP_TIME)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.federationHost);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidLocalFhsIdProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.PROVIDER_ID_KEY)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.federationHost);
    }
}
