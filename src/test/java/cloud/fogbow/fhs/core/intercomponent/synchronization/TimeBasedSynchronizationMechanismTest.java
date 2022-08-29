package cloud.fogbow.fhs.core.intercomponent.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;

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

    private DatabaseManager databaseManager;
    private TimeBasedSynchronizationMechanism syncMechanism;
    private FederationHost federationHost;
    private PropertiesHolder propertiesHolder;
    private FederationUpdate localUpdate1;
    private FederationUpdate localUpdate2;
    private FederationUpdate remoteUpdate1;
    private FederationUpdate remoteUpdate2;
    
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
        
        this.localUpdate1 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.localUpdate1.isLocal()).thenReturn(true);
        
        this.localUpdate2 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.localUpdate2.isLocal()).thenReturn(true);
        
        this.remoteUpdate1 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.remoteUpdate1.isLocal()).thenReturn(false);
        
        this.remoteUpdate2 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.remoteUpdate2.isLocal()).thenReturn(false);
        
        this.federationHost = Mockito.mock(FederationHost.class);
        
        this.databaseManager = Mockito.mock(DatabaseManager.class);
        Mockito.when(this.databaseManager.getUpdates()).thenReturn(Arrays.asList(localUpdate1, localUpdate2, remoteUpdate1, remoteUpdate2));
    }
    
    // test case: When calling the constructor, it must read the required properties
    // properly from the configuration.
    @Test
    public void testConstructorReadsPropertiesCorrectly() throws ConfigurationErrorException {
        this.syncMechanism = new TimeBasedSynchronizationMechanism(this.databaseManager, this.federationHost);
        
        assertEquals(3, this.syncMechanism.getAllowedFhssIds().size());
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_1));
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_2));
        assertTrue(this.syncMechanism.getAllowedFhssIds().contains(FHS_ID_3));
        assertEquals(SLEEP_TIME, this.syncMechanism.getSleepTime());
        
        assertEquals(2, this.syncMechanism.getLocalUpdates().size());
        assertTrue(this.syncMechanism.getLocalUpdates().contains(this.localUpdate1));
        assertTrue(this.syncMechanism.getLocalUpdates().contains(this.localUpdate2));
        
        assertEquals(2, this.syncMechanism.getRemoteUpdates().size());
        assertTrue(this.syncMechanism.getRemoteUpdates().contains(this.remoteUpdate1));
        assertTrue(this.syncMechanism.getRemoteUpdates().contains(this.remoteUpdate2));
    }
    
    // test case: When calling the constructor and the ALLOWED_FHS_IDS property is null, 
    // then it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidAllowedFhsIdsProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.databaseManager, this.federationHost);
    }
    
    // test case: When calling the constructor and the SYNCHRONIZATION_SLEEP_TIME property is null, 
    // then it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidSleepTimeProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.SYNCHRONIZATION_SLEEP_TIME)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.databaseManager, this.federationHost);
    }
    
    // test case: When calling the constructor and the PROVIDER_ID property is null, 
    // then it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorInvalidLocalFhsIdProperty() throws ConfigurationErrorException {
        Mockito.when(this.propertiesHolder.getProperty(
                ConfigurationPropertyKeys.PROVIDER_ID_KEY)).thenReturn(null);
        
        new TimeBasedSynchronizationMechanism(this.databaseManager, this.federationHost);
    }
}
