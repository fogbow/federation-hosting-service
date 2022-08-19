package cloud.fogbow.fhs.core.intercomponent.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.RemoteFederation;

// TODO documentation
public class FederationUpdateDaemonTest {
    private static final long SLEEP_TIME = 1L;
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
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String REMOTE_FEDERATION_FHS_1_ID_1 = "remoteFederationFhs1Id1";
    private static final String REMOTE_FEDERATION_FHS_1_NAME_1 = "remoteFederationFhs1Name1";
    private static final String REMOTE_FEDERATION_FHS_1_DESCRIPTION_1 = "remoteFederationFhs1Description1";
    private static final boolean REMOTE_FEDERATION_FHS_1_ENABLED_1 = true;
    private static final String REMOTE_FEDERATION_FHS_1_OWNER_1 = "remoteFederationFhs1Owner1";
    private static final String REMOTE_FEDERATION_FHS_1_ID_2 = "remoteFederationFhs1Id2";
    private static final String REMOTE_FEDERATION_FHS_1_NAME_2 = "remoteFederationFhs1Name2";
    private static final String REMOTE_FEDERATION_FHS_1_DESCRIPTION_2 = "remoteFederationFhs1Description2";
    private static final boolean REMOTE_FEDERATION_FHS_1_ENABLED_2 = true;
    private static final String REMOTE_FEDERATION_FHS_1_OWNER_2 = "remoteFederationFhs1Owner2";
    private static final String REMOTE_FEDERATION_FHS_2_ID_1 = "remoteFederationFhs1Id2";
    private static final String REMOTE_FEDERATION_FHS_2_NAME_1 = "remoteFederationFhs2Name1";
    private static final String REMOTE_FEDERATION_FHS_2_DESCRIPTION_1 = "remoteFederationFhs2Description1";
    private static final boolean REMOTE_FEDERATION_FHS_2_ENABLED_1 = true;
    private static final String REMOTE_FEDERATION_FHS_2_OWNER_1 = "remoteFederationFhs2Owner1";
    private static final String REMOTE_FEDERATION_FHS_2_ID_2 = "remoteFederationFhs2Id2";
    private static final String REMOTE_FEDERATION_FHS_2_NAME_2 = "remoteFederationFhs2Name2";
    private static final String REMOTE_FEDERATION_FHS_2_DESCRIPTION_2 = "remoteFederationFhs2Description2";
    private static final boolean REMOTE_FEDERATION_FHS_2_ENABLED_2 = true;
    private static final String REMOTE_FEDERATION_FHS_2_OWNER_2 = "remoteFederationFhs2Owner2";
    private static final String LOCAL_FHS_ID = "localFhsId";
    
    private DatabaseManager databaseManager;
    private FederationUpdateDaemon daemon;
    private List<FederationUpdate> localUpdates;
    private List<FederationUpdate> remoteUpdates;
    private FhsCommunicationMechanism communicationMechanism;
    private FederationHost federationHost;
    private List<String> allowedFhssIds;
    private Federation federation1;
    private Federation federation2;
    private FederationInstance federationInstance1;
    private FederationInstance federationInstance2;
    private FederationInstance remoteFederationInstance1Fhs1;
    private FederationInstance remoteFederationInstance2Fhs1;
    private FederationInstance remoteFederationInstance1Fhs2;
    private FederationInstance remoteFederationInstance2Fhs2;
    private List<RemoteFederation> remoteFederations;
    private FederationUpdate localFederationUpdate1;
    private FederationUpdate localFederationUpdate2;
    private FederationUpdate remoteFederationUpdate1;
    private FederationUpdate remoteFederationUpdate2;
    private LocalUpdateHandler localUpdateHandler;
    private RemoteUpdateHandler remoteUpdateHandler;
    
    @Before
    public void setUp() throws FogbowException {
        this.federation1 = Mockito.mock(Federation.class);
        Mockito.when(this.federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(this.federation1.getName()).thenReturn(FEDERATION_NAME_1);
        Mockito.when(this.federation1.getDescription()).thenReturn(FEDERATION_DESCRIPTION_1);
        Mockito.when(this.federation1.enabled()).thenReturn(FEDERATION_ENABLED_1);
        Mockito.when(this.federation1.getOwner()).thenReturn(FEDERATION_OWNER_1);
        
        this.federationInstance1 = new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1, FEDERATION_OWNER_1);
        
        this.federation2 = Mockito.mock(Federation.class);
        Mockito.when(this.federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(this.federation2.getName()).thenReturn(FEDERATION_NAME_2);
        Mockito.when(this.federation2.getDescription()).thenReturn(FEDERATION_DESCRIPTION_2);
        Mockito.when(this.federation2.enabled()).thenReturn(FEDERATION_ENABLED_2);
        Mockito.when(this.federation2.getOwner()).thenReturn(FEDERATION_OWNER_2);
        
        this.federationInstance2 = new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, FEDERATION_ENABLED_2, FEDERATION_OWNER_2);
        
        this.remoteFederationInstance1Fhs1 = new FederationInstance(REMOTE_FEDERATION_FHS_1_ID_1,
                REMOTE_FEDERATION_FHS_1_NAME_1, REMOTE_FEDERATION_FHS_1_DESCRIPTION_1, 
                REMOTE_FEDERATION_FHS_1_ENABLED_1, REMOTE_FEDERATION_FHS_1_OWNER_1);
        this.remoteFederationInstance2Fhs1 = new FederationInstance(REMOTE_FEDERATION_FHS_1_ID_2,
                REMOTE_FEDERATION_FHS_1_NAME_2, REMOTE_FEDERATION_FHS_1_DESCRIPTION_2, 
                REMOTE_FEDERATION_FHS_1_ENABLED_2, REMOTE_FEDERATION_FHS_1_OWNER_2);
        this.remoteFederationInstance1Fhs2 = new FederationInstance(REMOTE_FEDERATION_FHS_2_ID_1,
                REMOTE_FEDERATION_FHS_2_NAME_1, REMOTE_FEDERATION_FHS_2_DESCRIPTION_1, 
                REMOTE_FEDERATION_FHS_2_ENABLED_1, REMOTE_FEDERATION_FHS_2_OWNER_1);
        this.remoteFederationInstance2Fhs2 = new FederationInstance(REMOTE_FEDERATION_FHS_2_ID_2,
                REMOTE_FEDERATION_FHS_2_NAME_2, REMOTE_FEDERATION_FHS_2_DESCRIPTION_2, 
                REMOTE_FEDERATION_FHS_2_ENABLED_2, REMOTE_FEDERATION_FHS_2_OWNER_2);
        
        this.remoteFederations = new ArrayList<RemoteFederation>();
        this.remoteFederations.addAll(Arrays.asList(
                new RemoteFederation(REMOTE_FEDERATION_FHS_1_ID_1,
                        REMOTE_FEDERATION_FHS_1_NAME_1, REMOTE_FEDERATION_FHS_1_DESCRIPTION_1, 
                        REMOTE_FEDERATION_FHS_1_ENABLED_1, REMOTE_FEDERATION_FHS_1_OWNER_1, FHS_ID_1),
                new RemoteFederation(REMOTE_FEDERATION_FHS_1_ID_2,
                        REMOTE_FEDERATION_FHS_1_NAME_2, REMOTE_FEDERATION_FHS_1_DESCRIPTION_2, 
                        REMOTE_FEDERATION_FHS_1_ENABLED_2, REMOTE_FEDERATION_FHS_1_OWNER_2, FHS_ID_1),
                new RemoteFederation(REMOTE_FEDERATION_FHS_2_ID_1,
                        REMOTE_FEDERATION_FHS_2_NAME_1, REMOTE_FEDERATION_FHS_2_DESCRIPTION_1, 
                        REMOTE_FEDERATION_FHS_2_ENABLED_1, REMOTE_FEDERATION_FHS_2_OWNER_1, FHS_ID_2),
                new RemoteFederation(REMOTE_FEDERATION_FHS_2_ID_2,
                        REMOTE_FEDERATION_FHS_2_NAME_2, REMOTE_FEDERATION_FHS_2_DESCRIPTION_1, 
                        REMOTE_FEDERATION_FHS_2_ENABLED_2, REMOTE_FEDERATION_FHS_2_OWNER_2, FHS_ID_2)));
        
        this.communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        
        Mockito.when(this.communicationMechanism.syncFederations(Mockito.eq(FHS_ID_1), 
                Mockito.eq(Arrays.asList(this.federationInstance1, this.federationInstance2)))).thenReturn(
                        Arrays.asList(this.remoteFederationInstance1Fhs1, this.remoteFederationInstance2Fhs1));
        Mockito.when(this.communicationMechanism.syncFederations(Mockito.eq(FHS_ID_2),
                Mockito.eq(Arrays.asList(this.federationInstance1, this.federationInstance2)))).thenReturn(
                        Arrays.asList(this.remoteFederationInstance1Fhs2, this.remoteFederationInstance2Fhs2));
        
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.getFederations()).thenReturn(Arrays.asList(federation1, federation2));
        
        this.allowedFhssIds = Arrays.asList(FHS_ID_1, FHS_ID_2);
        
        this.localFederationUpdate1 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.localFederationUpdate1.completed()).thenReturn(true);
        this.localFederationUpdate2 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.localFederationUpdate2.completed()).thenReturn(false);
        this.remoteFederationUpdate1 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.remoteFederationUpdate1.completed()).thenReturn(false);
        this.remoteFederationUpdate2 = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.remoteFederationUpdate2.completed()).thenReturn(true);
        
        this.localUpdates = new ArrayList<FederationUpdate>();
        this.localUpdates.add(this.localFederationUpdate1);
        this.localUpdates.add(this.localFederationUpdate2);
        
        this.remoteUpdates = new ArrayList<FederationUpdate>();
        this.remoteUpdates.add(this.remoteFederationUpdate1);
        this.remoteUpdates.add(this.remoteFederationUpdate2);
        
        this.localUpdateHandler = Mockito.mock(LocalUpdateHandler.class);
        this.remoteUpdateHandler = Mockito.mock(RemoteUpdateHandler.class);
        
        this.databaseManager = Mockito.mock(DatabaseManager.class);
        
        this.daemon = new FederationUpdateDaemon(this.databaseManager, this.localUpdates, this.remoteUpdates, 
                this.communicationMechanism, this.federationHost, SLEEP_TIME, this.allowedFhssIds, LOCAL_FHS_ID, 
                this.localUpdateHandler, this.remoteUpdateHandler);
    }
    
    @Test
    public void testSuccessfulDoRun() throws FogbowException {
        this.daemon.doRun();
        
        Mockito.verify(this.federationHost).setRemoteFederationsList(Mockito.eq(this.remoteFederations));
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate1);
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate2);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate1);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate2);
        assertEquals(1, this.localUpdates.size());
        assertTrue(this.localUpdates.contains(localFederationUpdate2));
        Mockito.verify(this.databaseManager).removeUpdate(localFederationUpdate1);
        Mockito.verify(this.databaseManager).saveFederationUpdate(localFederationUpdate2);
        assertEquals(1, this.remoteUpdates.size());
        assertTrue(this.remoteUpdates.contains(remoteFederationUpdate1));
        Mockito.verify(this.databaseManager).removeUpdate(remoteFederationUpdate2);
        Mockito.verify(this.databaseManager).saveFederationUpdate(remoteFederationUpdate1);
    }
    
    @Test
    public void testLocalUpdateSynchronizationFailed() throws InvalidParameterException {
        Mockito.doThrow(InvalidParameterException.class).when(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate1);
        Mockito.when(this.localFederationUpdate1.completed()).thenReturn(false);
        
        this.daemon.doRun();
        
        Mockito.verify(this.federationHost).setRemoteFederationsList(Mockito.eq(this.remoteFederations));
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate1);
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate2);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate1);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate2);
        assertEquals(2, this.localUpdates.size());
        assertTrue(this.localUpdates.contains(localFederationUpdate1));
        assertTrue(this.localUpdates.contains(localFederationUpdate2));
        assertEquals(1, this.remoteUpdates.size());
        assertTrue(this.remoteUpdates.contains(remoteFederationUpdate1));
    }
    
    @Test
    public void testRemoteUpdateSynchronizationFailed() throws InvalidParameterException {
        Mockito.doThrow(InvalidParameterException.class).when(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate1);
        
        this.daemon.doRun();
        
        Mockito.verify(this.federationHost).setRemoteFederationsList(Mockito.eq(this.remoteFederations));
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate1);
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate2);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate1);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate2);
        assertEquals(1, this.localUpdates.size());
        assertTrue(this.localUpdates.contains(localFederationUpdate2));
        assertEquals(1, this.remoteUpdates.size());
        assertTrue(this.remoteUpdates.contains(remoteFederationUpdate1));
    }
    
    @Test
    public void testRemoteFederationSynchronizationFailed() throws FogbowException {
        Mockito.when(this.communicationMechanism.syncFederations(Mockito.eq(FHS_ID_1), 
                Mockito.eq(Arrays.asList(this.federationInstance1, this.federationInstance2)))).thenThrow(new FogbowException(""));
        
        this.remoteFederations = new ArrayList<RemoteFederation>();
        this.remoteFederations.addAll(Arrays.asList(
                new RemoteFederation(REMOTE_FEDERATION_FHS_2_ID_1,
                        REMOTE_FEDERATION_FHS_2_NAME_1, REMOTE_FEDERATION_FHS_2_DESCRIPTION_1, 
                        REMOTE_FEDERATION_FHS_2_ENABLED_1, REMOTE_FEDERATION_FHS_2_OWNER_1, FHS_ID_2),
                new RemoteFederation(REMOTE_FEDERATION_FHS_2_ID_2,
                        REMOTE_FEDERATION_FHS_2_NAME_2, REMOTE_FEDERATION_FHS_2_DESCRIPTION_1, 
                        REMOTE_FEDERATION_FHS_2_ENABLED_2, REMOTE_FEDERATION_FHS_2_OWNER_2, FHS_ID_2)));
        
        this.daemon.doRun();
        
        Mockito.verify(this.federationHost).setRemoteFederationsList(Mockito.eq(this.remoteFederations));
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate1);
        Mockito.verify(this.localUpdateHandler).handleLocalUpdate(localFederationUpdate2);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate1);
        Mockito.verify(this.remoteUpdateHandler).handleRemoteUpdate(remoteFederationUpdate2);
        assertEquals(1, this.localUpdates.size());
        assertTrue(this.localUpdates.contains(localFederationUpdate2));
        assertEquals(1, this.remoteUpdates.size());
        assertTrue(this.remoteUpdates.contains(remoteFederationUpdate1));
    }
}
