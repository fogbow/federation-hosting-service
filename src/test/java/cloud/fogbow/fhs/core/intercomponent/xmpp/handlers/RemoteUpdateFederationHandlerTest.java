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

import com.google.gson.reflect.TypeToken;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.UpdateFederationRequest;
import cloud.fogbow.fhs.core.utils.JsonUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RemoteFacade.class })
public class RemoteUpdateFederationHandlerTest {
    private static final String IQ_RESULT_FORMAT = "\n<iq type=\"result\" id=\"%s\" from=\"%s\" to=\"%s\"/>";
    private static final String IQ_ERROR_RESULT_FORMAT =
            "\n<iq type=\"error\" id=\"%s\" from=\"%s\" to=\"%s\">\n" +
                    "  <error code=\"500\" type=\"wait\">\n" +
                    "    <undefined-condition xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>\n" +
                    "    <text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></text>\n" +
                    "  </error>\n" +
                    "</iq>";
    private static final String PROVIDER_ID = "provider-id";
    private static final String REQUESTING_MEMBER = "requester";
    private static final String FEDERATION_UPDATE_STR = "federationUpdateStr";
    
    private RemoteUpdateFederationHandler handler;
    private RemoteFacade remoteFacade;
    private JsonUtils jsonUtils;
    private FederationUpdate federationUpdate;
    
    @Before
    public void setUp() throws FogbowException {
        this.federationUpdate = Mockito.mock(FederationUpdate.class);
        
        this.remoteFacade = Mockito.mock(RemoteFacade.class);
        
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(this.remoteFacade);
        
        jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(jsonUtils.fromJson(FEDERATION_UPDATE_STR, 
                new TypeToken<FederationUpdate>(){})).thenReturn(this.federationUpdate);
        
        this.handler = new RemoteUpdateFederationHandler(this.jsonUtils);
    }
    
    // test case: When calling the method handle, it must extract the FederationUpdate from the
    // IQ passed as argument and call the method updateFederation of the RemoteFacade.
    @Test
    public void testHandle() throws FogbowException {
        IQ iq = UpdateFederationRequest.marshal(PROVIDER_ID, FEDERATION_UPDATE_STR);
        iq.setFrom(REQUESTING_MEMBER);
        
        IQ result = this.handler.handle(iq);
        
        Mockito.verify(this.remoteFacade).updateFederation(REQUESTING_MEMBER, federationUpdate);
        
        String iqId = iq.getID();
        String expected = String.format(IQ_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER);
        
        assertEquals(expected, result.toString());
    }
    
    // test case: When calling the method handle and the updateFederation method of the RemoteFacade throws an exception, 
    // the handle method must create an error response IQ.
    @Test
    public void testHandleWhenThrowsException() throws Exception {
        Mockito.doThrow(new FogbowException("")).
            when(this.remoteFacade).
            updateFederation(REQUESTING_MEMBER, this.federationUpdate);

        IQ iq = UpdateFederationRequest.marshal(PROVIDER_ID, FEDERATION_UPDATE_STR);
        iq.setFrom(REQUESTING_MEMBER);

        IQ result = this.handler.handle(iq);

        Mockito.verify(this.remoteFacade).updateFederation(REQUESTING_MEMBER, federationUpdate);

        String iqId = iq.getID();
        String expected = String.format(IQ_ERROR_RESULT_FORMAT, iqId, SystemConstants.JID_SERVICE_NAME + 
                SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + PROVIDER_ID, REQUESTING_MEMBER);
        assertEquals(expected, result.toString());
    }
}
