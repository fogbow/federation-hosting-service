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

import com.google.gson.reflect.TypeToken;

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
public class RemoteSyncFederationsRequestTest {
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
    
    private static final String LOCAL_FEDERATION_ID_1 = "localFederationId1";
    private static final String LOCAL_FEDERATION_NAME_1 = "localFederationName1";
    private static final String LOCAL_FEDERATION_DESCRIPTION_1 = "localFederationDescription1";
    private static final boolean LOCAL_FEDERATION_ENABLED_1 = true;
    private static final String LOCAL_FEDERATION_OWNER_1 = "localFederationOwner1";

    private static final String LOCAL_FEDERATION_ID_2 = "localFederationId2";
    private static final String LOCAL_FEDERATION_NAME_2 = "localFederationName2";
    private static final String LOCAL_FEDERATION_DESCRIPTION_2 = "localFederationDescription2";
    private static final boolean LOCAL_FEDERATION_ENABLED_2 = true;
    private static final String LOCAL_FEDERATION_OWNER_2 = "localFederationOwner2";
    
    private static final String LOCAL_FEDERATIONS_STR = "localFederationsStr";
    private static final String REMOTE_FEDERATIONS_STR = "remoteFederationsStr";
    
    private RemoteSyncFederationsRequest syncFederationsRequest;
    private XmppComponentManager packetSender;
    private JsonUtils jsonUtils;
    private IQ response;
    private List<FederationInstance> remoteFederations;
    private List<FederationInstance> localFederations;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(XmppErrorConditionToExceptionTranslator.class);
        
        this.remoteFederations = new ArrayList<FederationInstance>();
        this.remoteFederations.add(new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1, FEDERATION_OWNER_1));
        this.remoteFederations.add(new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, FEDERATION_ENABLED_2, FEDERATION_OWNER_2));
        
        this.localFederations = new ArrayList<FederationInstance>();
        this.localFederations.add(new FederationInstance(LOCAL_FEDERATION_ID_1, LOCAL_FEDERATION_NAME_1, 
                LOCAL_FEDERATION_DESCRIPTION_1, LOCAL_FEDERATION_ENABLED_1, LOCAL_FEDERATION_OWNER_1));
        this.localFederations.add(new FederationInstance(LOCAL_FEDERATION_ID_2, LOCAL_FEDERATION_NAME_2, 
                LOCAL_FEDERATION_DESCRIPTION_2, LOCAL_FEDERATION_ENABLED_2, LOCAL_FEDERATION_OWNER_2));
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.toJson(this.localFederations)).thenReturn(LOCAL_FEDERATIONS_STR);
        Mockito.when(this.jsonUtils.toJson(this.remoteFederations)).thenReturn(REMOTE_FEDERATIONS_STR);
        Mockito.when(this.jsonUtils.fromJson(REMOTE_FEDERATIONS_STR, new TypeToken<List<FederationInstance>>(){})).
        thenReturn(this.remoteFederations);
        
        this.packetSender = Mockito.mock(XmppComponentManager.class);
        
        this.response = syncFederationsResponse(this.remoteFederations);
        Mockito.when(this.packetSender.syncSendPacket(Mockito.any(IQ.class))).thenReturn(response);
        
        this.syncFederationsRequest = new RemoteSyncFederationsRequest(packetSender, FHS_ID, this.localFederations, this.jsonUtils);
    }
    
    // test case: When calling the method send, it must call the packet sender passing an IQ with the correct format.
    @Test
    public void testSend() throws FogbowException {
        List<FederationInstance> federationInstances = this.syncFederationsRequest.send();
        
        IQ expectedIQ = RemoteSyncFederationsRequest.marshal(FHS_ID, LOCAL_FEDERATIONS_STR);
        IQMatcher matcher = new IQMatcher(expectedIQ);
        Mockito.verify(this.packetSender).syncSendPacket(Mockito.argThat(matcher));
        assertEquals(this.remoteFederations, federationInstances);
        PowerMockito.verifyStatic(XmppErrorConditionToExceptionTranslator.class);
        XmppErrorConditionToExceptionTranslator.handleError(response, FHS_ID);
    }

    private IQ syncFederationsResponse(List<FederationInstance> federationInstances) {
        IQ iqResponse = new IQ();
        Element queryEl = iqResponse.getElement().addElement(IqElement.QUERY.toString(), 
                RemoteMethod.SYNC_FEDERATIONS.toString());
        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(jsonUtils.toJson(federationInstances));

        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(federationInstances.getClass().getName());
        return iqResponse;
    }
}
