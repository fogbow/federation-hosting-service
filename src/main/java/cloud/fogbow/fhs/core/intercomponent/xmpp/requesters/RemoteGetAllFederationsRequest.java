package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;

// TODO test
public class RemoteGetAllFederationsRequest implements RemoteRequest<List<FederationInstance>>{
    private static final Logger LOGGER = Logger.getLogger(RemoteGetAllFederationsRequest.class);

    private String provider;
    private XmppComponentManager packetSender;
    
    public RemoteGetAllFederationsRequest(XmppComponentManager packetSender, String provider) {
        this.packetSender = packetSender;
        this.provider = provider;
    }

    @Override
    public List<FederationInstance> send() throws FogbowException {
        IQ iq = marshal(provider);
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) packetSender.syncSendPacket(iq);
        XmppErrorConditionToExceptionTranslator.handleError(response, provider);
        LOGGER.debug(Messages.Log.SUCCESS);
        return unmarshalFederationList(response);
    }

    public static IQ marshal(String provider) {
        IQ iq = new IQ(IQ.Type.get);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + provider);
        iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString());
        return iq;
    }

    private List<FederationInstance> unmarshalFederationList(IQ response) throws InternalServerErrorException {
        Element queryElement = response.getElement().element(IqElement.QUERY.toString());
        String listStr = queryElement.element(IqElement.FEDERATION_LIST.toString()).getText();

        List<FederationInstance> rulesList;
        try {
            Type listType = new TypeToken<List<FederationInstance>>(){}.getType();
            rulesList = new Gson().fromJson(listStr, listType);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        return rulesList;
    }
}
