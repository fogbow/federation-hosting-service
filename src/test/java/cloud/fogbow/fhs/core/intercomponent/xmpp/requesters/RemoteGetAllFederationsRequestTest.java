package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IQMatcher;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ XmppErrorConditionToExceptionTranslator.class })
public class RemoteGetAllFederationsRequestTest {
    private static final String FHS_ID = "fhsId";
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final boolean FEDERATION_ENABLED_1 = true;
    private static final String FEDERATION_OWNER_1 = "federationOwner1";

    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_NAME_2 = "federationName2";
    private static final String FEDERATION_DESCRIPTION_2 = "federationDescription2";
    private static final boolean FEDERATION_ENABLED_2 = true;
    private static final String FEDERATION_OWNER_2 = "federationOwner2";
    
    private RemoteGetAllFederationsRequest remoteGetAllFederationsRequest;
    private XmppComponentManager packetSender;
    private JsonUtils jsonUtils;
    private List<FederationInstance> federationInstances;
    private IQ response;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(XmppErrorConditionToExceptionTranslator.class);
        
        this.jsonUtils = new JsonUtils();
        
        this.federationInstances = new ArrayList<FederationInstance>();
        this.federationInstances.add(new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1, FEDERATION_OWNER_1));
        this.federationInstances.add(new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, FEDERATION_ENABLED_2, FEDERATION_OWNER_2));
        
        this.packetSender = Mockito.mock(XmppComponentManager.class);
        
        this.response = getAllFederationsResponse(this.federationInstances);
        Mockito.when(this.packetSender.syncSendPacket(Mockito.any(IQ.class))).thenReturn(response);
        
        this.remoteGetAllFederationsRequest = new RemoteGetAllFederationsRequest(packetSender, FHS_ID);
    }
    
    @Test
    public void testSend() throws FogbowException {
        List<FederationInstance> federationInstances = this.remoteGetAllFederationsRequest.send();
        
        IQ expectedIQ = RemoteGetAllFederationsRequest.marshal(FHS_ID);
        IQMatcher matcher = new IQMatcher(expectedIQ);
        Mockito.verify(this.packetSender).syncSendPacket(Mockito.argThat(matcher));
        assertEquals(this.federationInstances, federationInstances);
        PowerMockito.verifyStatic(XmppErrorConditionToExceptionTranslator.class);
        XmppErrorConditionToExceptionTranslator.handleError(response, FHS_ID);
    }

    private IQ getAllFederationsResponse(List<FederationInstance> federationInstances) {
        IQ iqResponse = new IQ();
        Element queryEl = iqResponse.getElement().addElement(IqElement.QUERY.toString(), 
                RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString());
        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(jsonUtils.toJson(federationInstances));

        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(federationInstances.getClass().getName());
        return iqResponse;
    }
}
