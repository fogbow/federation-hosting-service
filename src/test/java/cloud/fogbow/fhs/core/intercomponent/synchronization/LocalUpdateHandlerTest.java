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

// TODO documentation
public class LocalUpdateHandlerTest {
    private static final String TARGET_FEDERATION_ID = "federationId";
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String FHS_ID_3 = "fhsId3";
    private static final String REMOTE_FHS_ID = "remoteFhsId";
    private FederationHost federationHost;
    private FhsCommunicationMechanism communicationMechanism;
    private String localFhsId = "localFhsId";
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
        
        handler = new LocalUpdateHandler(this.federationHost, this.communicationMechanism, this.localFhsId);
    }
    
    @Test
    public void testHandleLocalUpdateOnLocalFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(localFhsId);
        
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
    
    @Test
    public void testHandleLocalUpdateOnLocalFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(localFhsId);
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
    
    @Test
    public void testHandleLocalUpdateOnLocalFederationTryingUpdateAgainAfterFail() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(localFhsId);
        
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
    
    @Test
    public void testHandleLocalUpdateOnRemoteFederation() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        Mockito.verify(this.update).setAsCompleted();
    }
    
    @Test
    public void testHandleLocalUpdateOnRemoteFederationUpdateFails() throws FogbowException {
        Mockito.when(this.federation.getFhsId()).thenReturn(REMOTE_FHS_ID);
        
        Mockito.doThrow(FogbowException.class).when(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        
        handler.handleLocalUpdate(update);
        
        Mockito.verify(this.communicationMechanism).updateFederation(REMOTE_FHS_ID, update);
        Mockito.verify(this.update, Mockito.never()).setAsCompleted();
    }
}
