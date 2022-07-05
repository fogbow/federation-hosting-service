package cloud.fogbow.fhs.core.intercomponent.synchronization;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.RemoteFederation;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class EventBasedSynchronizationMechanismTest {
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String ALLOWED_FHS_IDS_LIST_STR = 
            String.join(SystemConstants.ALLOWED_FHS_IDS_SEPARATOR, FHS_ID_1, FHS_ID_2);
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final Boolean FEDERATION_ENABLED_1 = true;
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_NAME_2 = "federationName2";
    private static final String FEDERATION_DESCRIPTION_2 = "federationDescription2";
    private static final Boolean FEDERATION_ENABLED_2 = true;
    private static final String FEDERATION_OWNER_2 = "federationOwner2";
    private static final String REMOTE_FHS_1_FEDERATION_ID_1 = "remoteFhs1FederationId1";
    private static final String REMOTE_FHS_1_FEDERATION_NAME_1 = "remoteFhs1FederationName1";
    private static final String REMOTE_FHS_1_FEDERATION_DESCRIPTION_1 = "remoteFhs1FederationDescription1";
    private static final Boolean REMOTE_FHS_1_FEDERATION_ENABLED_1 = true;
    private static final String REMOTE_FHS_1_FEDERATION_OWNER_1 = "remoteFhs1FederationOwner1";
    private static final String REMOTE_FHS_1_FEDERATION_ID_2 = "remoteFhs1FederationId2";
    private static final String REMOTE_FHS_1_FEDERATION_NAME_2 = "remoteFhs1FederationName2";
    private static final String REMOTE_FHS_1_FEDERATION_DESCRIPTION_2 = "remoteFhs1FederationDescription2";
    private static final Boolean REMOTE_FHS_1_FEDERATION_ENABLED_2 = true;
    private static final String REMOTE_FHS_1_FEDERATION_OWNER_2 = "remoteFhs1FederationOwner2";
    private static final String REMOTE_FHS_2_FEDERATION_ID_1 = "remoteFhs2FederationId1";
    private static final String REMOTE_FHS_2_FEDERATION_NAME_1 = "remoteFhs2FederationName1";
    private static final String REMOTE_FHS_2_FEDERATION_DESCRIPTION_1 = "remoteFhs2FederationDescription1";
    private static final Boolean REMOTE_FHS_2_FEDERATION_ENABLED_1 = true;
    private static final String REMOTE_FHS_2_FEDERATION_OWNER_1 = "remoteFhs2FederationOwner1";
    private static final String REMOTE_FHS_2_FEDERATION_ID_2 = "remoteFhs2FederationId2";
    private static final String REMOTE_FHS_2_FEDERATION_NAME_2 = "remoteFhs2FederationName2";
    private static final String REMOTE_FHS_2_FEDERATION_DESCRIPTION_2 = "remoteFhs2FederationDescription2";
    private static final Boolean REMOTE_FHS_2_FEDERATION_ENABLED_2 = true;
    private static final String REMOTE_FHS_2_FEDERATION_OWNER_2 = "remoteFhs2FederationOwner2";
    
    private EventBasedSynchronizationMechanism syncMechanism;
    private FederationHost federationHost;
    private PropertiesHolder propertiesHolder;
    private FhsCommunicationMechanism communicationMechanism;
    private List<String> remoteFedHostList;
    private List<FederationInstance> localFederationsInstances;
    private List<FederationInstance> fhs1FederationsInstances;
    private List<FederationInstance> fhs2FederationsInstances;
    private Federation federation1;
    private Federation federation2;
    private FederationInstance localFederationInstance1;
    private FederationInstance localFederationInstance2;
    private FederationInstance fhs1FederationInstance1;
    private FederationInstance fhs1FederationInstance2;
    private FederationInstance fhs2FederationInstance1;
    private FederationInstance fhs2FederationInstance2;
    private RemoteFederation fhs1Federation1;
    private RemoteFederation fhs1Federation2;
    private RemoteFederation fhs2Federation1;
    private RemoteFederation fhs2Federation2;
    
    @Before
    public void setUp() throws FogbowException {
        this.federation1 = Mockito.mock(Federation.class);
        Mockito.when(this.federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(this.federation1.getName()).thenReturn(FEDERATION_NAME_1);
        Mockito.when(this.federation1.getDescription()).thenReturn(FEDERATION_DESCRIPTION_1);
        Mockito.when(this.federation1.enabled()).thenReturn(FEDERATION_ENABLED_1);
        Mockito.when(this.federation1.getOwner()).thenReturn(FEDERATION_OWNER_1);
        
        this.federation2 = Mockito.mock(Federation.class);
        Mockito.when(this.federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(this.federation2.getName()).thenReturn(FEDERATION_NAME_2);
        Mockito.when(this.federation2.getDescription()).thenReturn(FEDERATION_DESCRIPTION_2);
        Mockito.when(this.federation2.enabled()).thenReturn(FEDERATION_ENABLED_2);
        Mockito.when(this.federation2.getOwner()).thenReturn(FEDERATION_OWNER_2);
        
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.getFederations()).thenReturn(Arrays.asList(federation1, federation2));
        
        this.localFederationInstance1 = new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1, FEDERATION_OWNER_1);
        this.localFederationInstance2 = new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, FEDERATION_ENABLED_2, FEDERATION_OWNER_2);
        
        this.localFederationsInstances = Arrays.asList(this.localFederationInstance1, this.localFederationInstance2);
        
        this.fhs1FederationInstance1 = new FederationInstance(REMOTE_FHS_1_FEDERATION_ID_1, REMOTE_FHS_1_FEDERATION_NAME_1, 
                REMOTE_FHS_1_FEDERATION_DESCRIPTION_1, REMOTE_FHS_1_FEDERATION_ENABLED_1, REMOTE_FHS_1_FEDERATION_OWNER_1);
        this.fhs1FederationInstance2 = new FederationInstance(REMOTE_FHS_1_FEDERATION_ID_2, REMOTE_FHS_1_FEDERATION_NAME_2, 
                REMOTE_FHS_1_FEDERATION_DESCRIPTION_2, REMOTE_FHS_1_FEDERATION_ENABLED_2, REMOTE_FHS_1_FEDERATION_OWNER_2);
        
        this.fhs1FederationsInstances = Arrays.asList(this.fhs1FederationInstance1, this.fhs1FederationInstance2);
        
        this.fhs2FederationInstance1 = new FederationInstance(REMOTE_FHS_2_FEDERATION_ID_1, REMOTE_FHS_2_FEDERATION_NAME_1, 
                REMOTE_FHS_2_FEDERATION_DESCRIPTION_1, REMOTE_FHS_2_FEDERATION_ENABLED_1, REMOTE_FHS_2_FEDERATION_OWNER_1);
        this.fhs2FederationInstance2 = new FederationInstance(REMOTE_FHS_2_FEDERATION_ID_2, REMOTE_FHS_2_FEDERATION_NAME_2, 
                REMOTE_FHS_2_FEDERATION_DESCRIPTION_2, REMOTE_FHS_2_FEDERATION_ENABLED_2, REMOTE_FHS_2_FEDERATION_OWNER_2);
        
        this.fhs2FederationsInstances = Arrays.asList(this.fhs2FederationInstance1, this.fhs2FederationInstance2);
        
        this.fhs1Federation1 = new RemoteFederation(REMOTE_FHS_1_FEDERATION_ID_1, REMOTE_FHS_1_FEDERATION_NAME_1, 
                REMOTE_FHS_1_FEDERATION_DESCRIPTION_1, true, REMOTE_FHS_1_FEDERATION_OWNER_1, FHS_ID_1);
        this.fhs1Federation2 = new RemoteFederation(REMOTE_FHS_1_FEDERATION_ID_2, REMOTE_FHS_1_FEDERATION_NAME_2, 
                REMOTE_FHS_1_FEDERATION_DESCRIPTION_2, true, REMOTE_FHS_1_FEDERATION_OWNER_2, FHS_ID_1);
        this.fhs2Federation1 = new RemoteFederation(REMOTE_FHS_2_FEDERATION_ID_1, REMOTE_FHS_2_FEDERATION_NAME_1, 
                REMOTE_FHS_2_FEDERATION_DESCRIPTION_1, true, REMOTE_FHS_2_FEDERATION_OWNER_1, FHS_ID_2);
        this.fhs2Federation2 = new RemoteFederation(REMOTE_FHS_2_FEDERATION_ID_2, REMOTE_FHS_2_FEDERATION_NAME_2, 
                REMOTE_FHS_2_FEDERATION_DESCRIPTION_2, true, REMOTE_FHS_2_FEDERATION_OWNER_2, FHS_ID_2);
        
        this.communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        Mockito.when(this.communicationMechanism.syncFederations(FHS_ID_1, 
                this.localFederationsInstances)).thenReturn(fhs1FederationsInstances);
        Mockito.when(this.communicationMechanism.syncFederations(FHS_ID_2, 
                this.localFederationsInstances)).thenReturn(fhs2FederationsInstances);
        
        this.remoteFedHostList = Arrays.asList(FHS_ID_1, FHS_ID_2);
        
        this.syncMechanism = new EventBasedSynchronizationMechanism(communicationMechanism, federationHost, remoteFedHostList);
    }
    
    @Test
    public void testConstructor() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(ALLOWED_FHS_IDS_LIST_STR);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.syncMechanism = new EventBasedSynchronizationMechanism(federationHost);
        
        List<String> expectedRemoteFedHosts = Arrays.asList(FHS_ID_1, FHS_ID_2);
        assertEquals(expectedRemoteFedHosts, this.syncMechanism.getRemoteFedHosts());
    }
    
    @Test
    public void testConstructorWithNoAllowedFhs() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn("");
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.syncMechanism = new EventBasedSynchronizationMechanism(federationHost);
        
        List<String> expectedRemoteFedHosts = Arrays.asList();
        assertEquals(expectedRemoteFedHosts, this.syncMechanism.getRemoteFedHosts());
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorWithNullAllowedFhsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(null);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.syncMechanism = new EventBasedSynchronizationMechanism(federationHost);
    }
    
    @Test
    public void testOnStartUp() throws FogbowException {
        this.syncMechanism.onStartUp();
        
        List<RemoteFederation> expectedRemoteFederations = Arrays.asList(this.fhs1Federation1, this.fhs1Federation2, 
                this.fhs2Federation1, this.fhs2Federation2);
        Mockito.verify(this.federationHost).setRemoteFederationsList(expectedRemoteFederations);
    }
    
    @Test
    public void testOnStartUpCommunicationWithHostFails() throws FogbowException {
        Mockito.when(this.communicationMechanism.syncFederations(FHS_ID_1, 
                this.localFederationsInstances)).thenThrow(new FogbowException(""));
        Mockito.when(this.communicationMechanism.syncFederations(FHS_ID_2, 
                this.localFederationsInstances)).thenReturn(fhs2FederationsInstances);
        
        this.syncMechanism.onStartUp();
        
        List<RemoteFederation> expectedRemoteFederations = Arrays.asList(this.fhs2Federation1, this.fhs2Federation2);
        Mockito.verify(this.federationHost).setRemoteFederationsList(expectedRemoteFederations);
    }
}
