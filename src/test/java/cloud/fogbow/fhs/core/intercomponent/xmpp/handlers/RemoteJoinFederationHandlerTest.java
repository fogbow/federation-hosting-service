package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteJoinFederationRequest;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.utils.JsonUtils;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemoteFacade.class })
public class RemoteJoinFederationHandlerTest {
    private static final String IQ_RESULT_FORMAT = "\n<iq type=\"result\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
            "  <query xmlns=\"remoteJoinFederation\">\n" +
            "    <remoteFederation>%s</remoteFederation>\n" +
            "  </query>\n" +
            "</iq>";
    private static final String IQ_ERROR_RESULT_FORMAT =
            "\n<iq type=\"error\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
                    "  <error code=\"500\" type=\"wait\">\n" +
                    "    <undefined-condition xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>\n" +
                    "    <text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></text>\n" +
                    "  </error>\n" +
                    "</iq>";
    private static final String FEDERATION_ID = "federation-id";
    private static final String PROVIDER_ID = "provider-id";
    private static final String REQUESTING_MEMBER = "requester";
    private static final String FEDERATION_STR = "federation-str";
    private static final String REQUESTER_STR = "requester-str";
    
    private RemoteJoinFederationHandler handler;
    private FederationUser requester;
    private RemoteFacade remoteFacade;
    private Federation federation;
    private JsonUtils jsonUtils;
    
    @Before
    public void setUp() throws FogbowException {
        this.requester = Mockito.mock(FederationUser.class);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.fromJson(REQUESTER_STR, FederationUser.class)).thenReturn(requester);
        
        this.federation = Mockito.mock(Federation.class);
        Mockito.when(this.federation.toJson()).thenReturn(FEDERATION_STR);
        
        this.remoteFacade = Mockito.mock(RemoteFacade.class);
        Mockito.when(this.remoteFacade.joinFederation(REQUESTING_MEMBER, requester, FEDERATION_ID)).thenReturn(federation);
        
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(this.remoteFacade);
        
        this.handler = new RemoteJoinFederationHandler(this.jsonUtils);
    }
    
    @Test
    public void testHandle() throws FogbowException {
        IQ iq = RemoteJoinFederationRequest.marshal(REQUESTER_STR, FEDERATION_ID, PROVIDER_ID);
        iq.setFrom(REQUESTING_MEMBER);
        
        IQ result = this.handler.handle(iq);
        
        Mockito.verify(this.remoteFacade).joinFederation(REQUESTING_MEMBER, requester, FEDERATION_ID);
        
        String iqId = iq.getID();
        String expected = String.format(IQ_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER,
                FEDERATION_STR);
        assertEquals(expected, result.toString());
    }
    
    @Test
    public void testHandleWhenThrowsException() throws Exception {
        Mockito.when(this.remoteFacade.joinFederation(REQUESTING_MEMBER, requester, FEDERATION_ID)).thenThrow(new FogbowException(""));

        IQ iq = RemoteJoinFederationRequest.marshal(REQUESTER_STR, FEDERATION_ID, PROVIDER_ID);
        iq.setFrom(REQUESTING_MEMBER);

        IQ result = this.handler.handle(iq);

        Mockito.verify(this.remoteFacade).joinFederation(REQUESTING_MEMBER, requester, FEDERATION_ID);

        String iqId = iq.getID();
        String expected = String.format(IQ_ERROR_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER);
        assertEquals(expected, result.toString());
    }
}
