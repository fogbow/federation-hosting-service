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

import com.google.gson.reflect.TypeToken;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteSyncFederationsRequest;
import cloud.fogbow.fhs.core.utils.JsonUtils;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemoteFacade.class })
public class RemoteSyncFederationsHandlerTest {
    private static final String IQ_RESULT_FORMAT = "\n<iq type=\"result\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
            "  <query xmlns=\"syncFederations\">\n" +
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
    private static final String REQUESTING_MEMBER = "requester";
    private static final String PROVIDER_ID = "provider-id";
    private static final String LOCAL_FEDERATIONS_STR = "localFederations";
    private static final String REMOTE_FEDERATIONS_STR = "remoteFederations";
    private JsonUtils jsonUtils;
    private FederationInstance localFederation1;
    private RemoteSyncFederationsHandler handler;
    private RemoteFacade remoteFacade;
    private FederationInstance remoteFederation1;
    private List<FederationInstance> remoteFederationInstances;
    private List<FederationInstance> localFederationInstances;
    
    @Before
    public void setUp() throws FogbowException {
        this.localFederation1 = Mockito.mock(FederationInstance.class);
        this.remoteFederation1 = Mockito.mock(FederationInstance.class);
        
        this.localFederationInstances = new ArrayList<FederationInstance>();
        this.localFederationInstances.add(localFederation1);
        
        this.remoteFederationInstances = new ArrayList<FederationInstance>();
        this.remoteFederationInstances.add(remoteFederation1);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.fromJson(LOCAL_FEDERATIONS_STR, new TypeToken<List<FederationInstance>>(){})).thenReturn(localFederationInstances);
        Mockito.when(this.jsonUtils.toJson(remoteFederationInstances)).thenReturn(REMOTE_FEDERATIONS_STR);
        
        this.remoteFacade = Mockito.mock(RemoteFacade.class);
        Mockito.when(this.remoteFacade.getFederationList(REQUESTING_MEMBER)).thenReturn(remoteFederationInstances);
        
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(this.remoteFacade);
        
        this.handler = new RemoteSyncFederationsHandler(this.jsonUtils);
    }
    
    @Test
    public void testHandle() throws FogbowException {
        IQ iq = RemoteSyncFederationsRequest.marshal(PROVIDER_ID, LOCAL_FEDERATIONS_STR);
        iq.setFrom(REQUESTING_MEMBER);
        
        IQ result = this.handler.handle(iq);
        
        Mockito.verify(this.remoteFacade).updateRemoteFederationList(REQUESTING_MEMBER, localFederationInstances);
        Mockito.verify(this.remoteFacade).getFederationList(REQUESTING_MEMBER);
        
        String iqId = iq.getID();
        String expected = String.format(IQ_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER,
                REMOTE_FEDERATIONS_STR);
        assertEquals(expected, result.toString());
    }
    
    @Test
    public void testHandleWhenThrowsException() throws Exception {
        Mockito.doThrow(new FogbowException("")).when(this.remoteFacade).
            updateRemoteFederationList(REQUESTING_MEMBER, localFederationInstances);

        IQ iq = RemoteSyncFederationsRequest.marshal(PROVIDER_ID, LOCAL_FEDERATIONS_STR);
        iq.setFrom(REQUESTING_MEMBER);

        IQ result = this.handler.handle(iq);

        Mockito.verify(this.remoteFacade).updateRemoteFederationList(REQUESTING_MEMBER, localFederationInstances);

        String iqId = iq.getID();
        String expected = String.format(IQ_ERROR_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER);
        assertEquals(expected, result.toString());
    }
}
