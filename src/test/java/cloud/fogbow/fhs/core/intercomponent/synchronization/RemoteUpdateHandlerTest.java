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

// TODO documentation
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
    
    @Test
    public void testHandleRemoteUpdateOnRemoteFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);

        updateHandler.handleRemoteUpdate(federationUpdate);
        
        Mockito.verify(this.federatioHost).updateFederationUsingRemoteData(federationUpdate);
        Mockito.verifyZeroInteractions(this.communicationMechanism);
        Mockito.verify(this.federationUpdate).setAsCompleted();
    }
    
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
