package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IQMatcher;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ XmppErrorConditionToExceptionTranslator.class })
public class UpdateFederationRequestTest {
    private static final String FHS_ID = "fhsId";
    private static final String FEDERATION_UPDATE_STR = "federationUpdateStr";
    
    private UpdateFederationRequest remoteUpdateFederationRequest;
    private XmppComponentManager packetSender;
    private JsonUtils jsonUtils;
    private IQ response;
    private FederationUpdate federationUpdate;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(XmppErrorConditionToExceptionTranslator.class);
        
        this.federationUpdate = Mockito.mock(FederationUpdate.class);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.toJson(this.federationUpdate)).thenReturn(FEDERATION_UPDATE_STR);

        this.packetSender = Mockito.mock(XmppComponentManager.class);
        
        this.response = new IQ();
        Mockito.when(this.packetSender.syncSendPacket(Mockito.any(IQ.class))).thenReturn(response);
        
        this.remoteUpdateFederationRequest = new UpdateFederationRequest(packetSender, FHS_ID, 
                this.federationUpdate, this.jsonUtils);
    }
    
    // test case: When calling the method send, it must call the packet sender passing an IQ with the correct format.
    @Test
    public void testSend() throws FogbowException {
        this.remoteUpdateFederationRequest.send();
        
        IQ expectedIQ = UpdateFederationRequest.marshal(FHS_ID, FEDERATION_UPDATE_STR);
        IQMatcher matcher = new IQMatcher(expectedIQ);
        Mockito.verify(this.packetSender).syncSendPacket(Mockito.argThat(matcher));
        PowerMockito.verifyStatic(XmppErrorConditionToExceptionTranslator.class);
        XmppErrorConditionToExceptionTranslator.handleError(response, FHS_ID);
    }
}
