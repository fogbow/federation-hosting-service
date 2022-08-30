package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import static org.junit.Assert.assertEquals;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IQMatcher;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationFactory;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.utils.JsonUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ XmppErrorConditionToExceptionTranslator.class })
public class RemoteJoinFederationRequestTest {
    private static final String FHS_ID = "fhsId";
    private static final String FEDERATION_ID = "federationId";
    private static final String REQUESTER_STR = "requesterStr";
    private static final String FEDERATION_STR = "federationStr";
    
    private RemoteJoinFederationRequest remoteJoinFederationRequest;
    private XmppComponentManager packetSender;
    private JsonUtils jsonUtils;
    private IQ response;
    private FederationUser requesterUser;
    private FederationFactory federationFactory;
    private Federation federation;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(XmppErrorConditionToExceptionTranslator.class);
        
        this.requesterUser = Mockito.mock(FederationUser.class);
        
        this.federation = Mockito.mock(Federation.class);
        Mockito.when(this.federation.toJson()).thenReturn(FEDERATION_STR);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.toJson(requesterUser)).thenReturn(REQUESTER_STR);
        
        this.federationFactory = Mockito.mock(FederationFactory.class);
        Mockito.when(this.federationFactory.createFederation(FEDERATION_STR)).thenReturn(federation);

        this.packetSender = Mockito.mock(XmppComponentManager.class);
        
        this.response = joinFederationResponse(this.federation);
        Mockito.when(this.packetSender.syncSendPacket(Mockito.any(IQ.class))).thenReturn(response);
        
        this.remoteJoinFederationRequest = new RemoteJoinFederationRequest(packetSender, requesterUser, 
                FEDERATION_ID, FHS_ID, federationFactory, jsonUtils);
    }
    
    // test case: When calling the method send, it must call the packet sender passing an IQ with the correct format.
    @Test
    public void testSend() throws FogbowException {
        Federation returnedFederation = this.remoteJoinFederationRequest.send();
        
        IQ expectedIQ = RemoteJoinFederationRequest.marshal(REQUESTER_STR, FEDERATION_ID, FHS_ID);
        IQMatcher matcher = new IQMatcher(expectedIQ);
        Mockito.verify(this.packetSender).syncSendPacket(Mockito.argThat(matcher));
        assertEquals(this.federation, returnedFederation);
        PowerMockito.verifyStatic(XmppErrorConditionToExceptionTranslator.class);
        XmppErrorConditionToExceptionTranslator.handleError(response, FHS_ID);
    }

    private IQ joinFederationResponse(Federation federation) {
        IQ iqResponse = new IQ();
        Element queryEl = iqResponse.getElement().addElement(IqElement.QUERY.toString(), 
                RemoteMethod.REMOTE_JOIN_FEDERATION.toString());
        Element federationElement = queryEl.addElement(IqElement.REMOTE_FEDERATION.toString());
        federationElement.setText(federation.toJson());
        return iqResponse;
    }    
}
