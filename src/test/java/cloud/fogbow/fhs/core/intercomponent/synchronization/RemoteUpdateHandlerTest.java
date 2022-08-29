package cloud.fogbow.fhs.core.intercomponent.synchronization;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;

public class RemoteUpdateHandlerTest {
    private static final String TARGET_FEDERATION_ID = "federationId";
    private static final String LOCAL_FHS_ID = "localFhsId";
    private static final String REMOTE_FHS_ID = "remoteFhsId";
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String FHS_ID_3 = "fhsId3";
    
    private RemoteUpdateHandler updateHandler;
    private FederationUpdate federationUpdate;
    private FederationHost federatioHost;
    private FhsCommunicationMechanism communicationMechanism;
    private Federation federation;
    
    @Before
    public void setUp() throws InvalidParameterException {
        this.communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        
        this.federation = Mockito.mock(Federation.class);
        Mockito.when(this.federation.getSupportingFhss()).thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        this.federatioHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federatioHost.getFederation(TARGET_FEDERATION_ID)).thenReturn(federation);
        
        this.federationUpdate = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.federationUpdate.getTargetFederationId()).thenReturn(TARGET_FEDERATION_ID);
        
        updateHandler = new RemoteUpdateHandler(this.federatioHost, this.communicationMechanism, LOCAL_FHS_ID);
    }
    
    // test case: When calling the handleRemoteUpdate method and the local FHS is the target federation's owner, 
    // it must call the updateFederationUsingRemoteData from the FederationHost and update the supporting FHSs
    // by calling the method updateFederation of the FhsCommunicationMechanism. Finally, if all the updates
    // are successful, it must set the update as completed.
    @Test
    public void testHandleRemoteUpdateOnLocalFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        
        Mockito.when(this.federationUpdate.getUpdatedFhss()).
        thenReturn(Arrays.asList()).
        thenReturn(Arrays.asList(FHS_ID_1)).
        thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2)).
        thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        updateHandler.handleRemoteUpdate(federationUpdate);
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_2, federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_3, federationUpdate);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.federationUpdate).setAsCompleted();
    }
    
    // test case: When calling the handleRemoteUpdate method and the call to updateFederationUsingRemoteData
    // throws an InvalidParameterException, the method must rethrow the exception and not update the
    // other FHSs.
    @Test
    public void testHandleRemoteUpdateOnLocalFederationLocalUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        Mockito.doThrow(InvalidParameterException.class).when(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        
        try {
            updateHandler.handleRemoteUpdate(federationUpdate);
            Assert.fail("Expected InvalidParameterException.");
        } catch (InvalidParameterException e) {
            
        }
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_1, federationUpdate);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_2, federationUpdate);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_3, federationUpdate);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.federationUpdate, Mockito.never()).setAsCompleted();
    }
    
    // test case: When calling the handleRemoteUpdate and the call to updateFederation for a 
    // FHS fails, then the method must not add the FHS to the list of updated FHSs and must perform
    // the update for the other FHSs normally.
    @Test
    public void testHandleRemoteUpdateOnLocalFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        
        this.federationUpdate = Mockito.mock(FederationUpdate.class);
        Mockito.when(this.federationUpdate.getTargetFederationId()).thenReturn(TARGET_FEDERATION_ID);
        Mockito.when(this.federationUpdate.getUpdatedFhss()).
        thenReturn(Arrays.asList()).
        thenReturn(Arrays.asList()).
        thenReturn(Arrays.asList(FHS_ID_2)).
        thenReturn(Arrays.asList(FHS_ID_2, FHS_ID_3));
        
        Mockito.doThrow(FogbowException.class).when(this.communicationMechanism).updateFederation(FHS_ID_1, federationUpdate);
        
        updateHandler.handleRemoteUpdate(federationUpdate);
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_2, federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_3, federationUpdate);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.federationUpdate, Mockito.never()).setAsCompleted();
    }
    
    // test case: When calling the handleRemoteUpdate passing an update which failed for an FHS, 
    // the method must call the updateFederation method of the CommunicationMechanism only for the FHS that failed.
    // Then, if the update is successful, it must add the FHS to the list of updated FHSs and set the 
    // update as completed.
    @Test
    public void testHandleRemoteUpdateOnLocalFederationTryingUpdateAgainAfterFail() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(LOCAL_FHS_ID);
        
        Mockito.when(this.federationUpdate.getUpdatedFhss()).
        thenReturn(Arrays.asList(FHS_ID_2, FHS_ID_3)).
        thenReturn(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
        
        updateHandler.handleRemoteUpdate(federationUpdate);
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verify(this.communicationMechanism).updateFederation(FHS_ID_1, federationUpdate);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_2, federationUpdate);
        Mockito.verify(this.communicationMechanism, Mockito.never()).updateFederation(FHS_ID_3, federationUpdate);
        Mockito.verify(this.federationUpdate).addUpdatedFhs(FHS_ID_1);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_2);
        Mockito.verify(this.federationUpdate, Mockito.never()).addUpdatedFhs(FHS_ID_3);
        Mockito.verify(this.federationUpdate).setAsCompleted();
    }
    
    // test case: When calling the handleRemoteUpdate and the local FHS is not the target federation's owner, 
    // then it must call the updateFederationsUsingRemoteData of the FederationHost and set the update
    // as completed, without updating other FHSs.
    @Test
    public void testHandleRemoteUpdateOnRemoteFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);

        updateHandler.handleRemoteUpdate(federationUpdate);
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verifyZeroInteractions(this.communicationMechanism);
        Mockito.verify(this.federationUpdate).setAsCompleted();
    }
    
    // test case: When calling the handleRemoteUpdate, if the local FHS is not the target federation's owner
    // and the call to updateFederationsUsingRemoteData throws an InvalidParameterException, then the
    // method must not set the update as completed.
    @Test
    public void testHandleRemoteUpdateOnRemoteFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);
        
        Mockito.doThrow(InvalidParameterException.class).when(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);

        try {
            updateHandler.handleRemoteUpdate(federationUpdate);
            Assert.fail("Expected InvalidParameterException.");
        } catch (InvalidParameterException e) {
            
        }
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verifyZeroInteractions(this.communicationMechanism);
        Mockito.verify(this.federationUpdate, Mockito.never()).setAsCompleted();
    }
}
