package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;

public class LocalUpdateHandlerTest {
    private static final String TARGET_FEDERATION_ID = "federationId";
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String FHS_ID_3 = "fhsId3";
    private static final String LOCAL_FHS_ID = "localFhsId";
    private static final String REMOTE_FHS_ID = "remoteFhsId";

    private FederationHost federationHost;
    private FhsCommunicationMechanism communicationMechanism;
    private LocalUpdateHandler handler;
    private FederationUpdate update;
    private Federation federation;
    
    @Before
    public void setUp() throws InvalidParameterException {
        this.communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        
        this.federation = Mockito.mock(Federation.class);
        Mockito.when(this.federation.getSupportingFhss()).thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.getFederation(TARGET_FEDERATION_ID)).thenReturn(federation);
        
        this.update = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.update.getTargetFederationId()).thenReturn(TARGET_FEDERATION_ID);
        
        handler = new LocalUpdateHandler(this.federationHost, this.communicationMechanism, LOCAL_FHS_ID);
    }
    
    // test case: When calling the method handleLocalUpdate, it must call the method updateFederation of 
    // the FhsCommunicationMechanism for all supporting FHSs of the target federation, passing the update.
    // The method must add the updated FHSs to the list of updatedFhss of the FederationUpdate and, finally,
    // set the update as completed.
    @Test
    public void testHandleLocalUpdateOnLocalFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        
        Mockito.when(this.update.getUpdatedFhss()).
            thenReturn(Arrays.asList()).
            thenReturn(Arrays.asList(FHS_ID_1)).
            thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2)).
            thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, update);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_2, update);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_3, update);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.update).setAsCompleted();
    }
    
    // test case: When calling the method handleLocalUpdate and the call to updateFederation on a
    // FHS throws an exception, the method must not add the FHS to the list of updated FHSs of the FederationUpdate
    // and must perform the update on the other FHSs.
    @Test
    public void testHandleLocalUpdateOnLocalFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        Mockito.when(this.update.getUpdatedFhss()).
            thenReturn(Arrays.asList()).
            thenReturn(Arrays.asList()).
            thenReturn(Arrays.asList(FHS_ID_2)).
            thenReturn(Arrays.asList(FHS_ID_2, FHS_ID_3));
        
        Mockito.doThrow(FogbowException.class).when(this.communicationMechanism).updateFederation(FHS_ID_1, update);
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, update);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_2, update);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_3, update);
        Mockito.verify(this.update, Mockito.never()).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.update, Mockito.never()).setAsCompleted();
    }
    
    // test case: When calling the method handleLocalUpdate passing an update which failed on an FHS, 
    // it must retry the update only on the FHS that failed. If the update is successful, it must add the FHS 
    // to the list of updated FHSs and set the update as completed.
    @Test
    public void testHandleLocalUpdateOnLocalFederationTryingUpdateAgainAfterFail() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        
        Mockito.when(this.update.getUpdatedFhss()).
            thenReturn(Arrays.asList(FHS_ID_2, FHS_ID_3)).
            thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, update);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_2, update);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_3, update);
        Mockito.verify(this.update).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.update).setAsCompleted();
    }
    
    // test case: When calling the method handleLocalUpdate and the target federation is not owned 
    // by the local FHS, it must call the CommunicationMechanism to send the update to the federation owner
    // FHS and set the update as completed.
    @Test
    public void testHandleLocalUpdateOnRemoteFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        Mockito.verify(this.update).setAsCompleted();
    }
    
    // test case: When calling the method handleLocalUpdate on a target federation that is not owned
    // by the local FHS and the update fails, the method must not set the update as completed.
    @Test
    public void testHandleLocalUpdateOnRemoteFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);
        
        Mockito.doThrow(FogbowException.class).when(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        Mockito.verify(this.update, Mockito.never()).setAsCompleted();
    }
}
