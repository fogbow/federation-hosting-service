package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteGetAllFederationsRequest;
import cloud.fogbow.fhs.core.utils.JsonUtils;
import cloud.fogbow.ras.constants.SystemConstants;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({RemoteFacade.class })
public class RemoteGetAllFederationsHandlerTest {
    private static final String IQ_RESULT_FORMAT = "\n<iq type=\"result\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
            "  <query xmlns=\"remoteGetAllFederations\">\n" +
            "    <federationList>%s</federationList>\n" +
            "    <federationListClassName>java.util.ArrayList</federationListClassName>\n" +
            "  </query>\n" +
            "</iq>";
    private static final String IQ_ERROR_RESULT_FORMAT =
            "\n<iq type=\"error\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
                    "  <error code=\"500\" type=\"wait\">\n" +
                    "    <undefined-condition xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>\n" +
                    "    <text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></text>\n" +
                    "  </error>\n" +
                    "</iq>";
    private static final String PROVIDER_ID = "provider-id";
    private static final String REQUESTING_MEMBER = "requester";
    private static final String FEDERATION_INSTANCES_LIST_STRING = "federationInstances";
    
    private RemoteGetAllFederationsHandler handler;
    private RemoteFacade remoteFacade;
    private FederationInstance federation1;
    private FederationInstance federation2;
    private JsonUtils jsonUtils;
    
    @Before
    public void setUp() throws FogbowException {
        this.federation1 = Mockito.mock(FederationInstance.class);
        this.federation2 = Mockito.mock(FederationInstance.class);
        
        List<FederationInstance> federationInstances = new ArrayList<FederationInstance>();
        federationInstances.add(federation1);
        federationInstances.add(federation2);
        
        this.remoteFacade = Mockito.mock(RemoteFacade.class);
        Mockito.when(this.remoteFacade.getFederationList(REQUESTING_MEMBER)).thenReturn(federationInstances);
        
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(this.remoteFacade);
        
        jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(jsonUtils.toJson(federationInstances)).thenReturn(FEDERATION_INSTANCES_LIST_STRING);
        
        this.handler = new RemoteGetAllFederationsHandler(this.jsonUtils);
    }
    
    @Test
    public void testHandle() throws FogbowException {
        IQ iq = RemoteGetAllFederationsRequest.marshal(PROVIDER_ID);
        iq.setFrom(REQUESTING_MEMBER);
        
        IQ result = this.handler.handle(iq);
        
        Mockito.verify(this.remoteFacade).getFederationList(REQUESTING_MEMBER);
        
        String iqId = iq.getID();
        String expected = String.format(IQ_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER,
                FEDERATION_INSTANCES_LIST_STRING);
        assertEquals(expected, result.toString());
    }
    
    @Test
    public void testHandleWhenThrowsException() throws Exception {
        Mockito.when(this.remoteFacade.getFederationList(REQUESTING_MEMBER)).thenThrow(new FogbowException(""));

        IQ iq = RemoteGetAllFederationsRequest.marshal(PROVIDER_ID);
        iq.setFrom(REQUESTING_MEMBER);

        IQ result = this.handler.handle(iq);

        Mockito.verify(this.remoteFacade).getFederationList(REQUESTING_MEMBER);

        String iqId = iq.getID();
        String expected = String.format(IQ_ERROR_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER);
        assertEquals(expected, result.toString());
    }
}
