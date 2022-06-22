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

public class RemoteSyncFederationsRequest  implements RemoteRequest<List<FederationInstance>>{
    private static final Logger LOGGER = Logger.getLogger(RemoteSyncFederationsRequest.class);

    private String provider;
    private XmppComponentManager packetSender;
    private List<FederationInstance> localFederations;
    
    public RemoteSyncFederationsRequest(XmppComponentManager packetSender, String provider,
            List<FederationInstance> localFederations) {
        this.packetSender = packetSender;
        this.provider = provider;
        this.localFederations = localFederations;
    }

    @Override
    public List<FederationInstance> send() throws FogbowException {
        IQ iq = marshal();
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) packetSender.syncSendPacket(iq);
        XmppErrorConditionToExceptionTranslator.handleError(response, provider);
        LOGGER.debug(Messages.Log.SUCCESS);
        return unmarshalFederationList(response);
    }

    private IQ marshal() {
        IQ iq = new IQ(IQ.Type.get);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + provider);
        Element queryEl = iq.getElement().addElement(IqElement.QUERY.toString(), RemoteMethod.SYNC_FEDERATIONS.toString());

        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(localFederations.getClass().getName());

        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(new Gson().toJson(localFederations));
        
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
