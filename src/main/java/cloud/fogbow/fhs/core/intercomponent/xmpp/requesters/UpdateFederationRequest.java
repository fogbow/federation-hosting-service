package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

// TODO test
public class UpdateFederationRequest implements RemoteRequest<Void>{
    private static final Logger LOGGER = Logger.getLogger(UpdateFederationRequest.class);

    private String provider;
    private XmppComponentManager packetSender;
    private FederationUpdate federationUpdate;
    private JsonUtils jsonUtils;

    public UpdateFederationRequest(XmppComponentManager packetSender, String remoteHost, 
            FederationUpdate update, JsonUtils jsonUtils) {
        this.packetSender = packetSender;
        this.provider = remoteHost;
        this.federationUpdate = update;
        this.jsonUtils = jsonUtils;
    }
    
    public UpdateFederationRequest(XmppComponentManager packetSender, String remoteHost,
            FederationUpdate update) {
        this(packetSender, remoteHost, update, new JsonUtils());
    }
    
    public Void send() throws FogbowException {
        IQ iq = marshal(provider, this.jsonUtils.toJson(federationUpdate));
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) packetSender.syncSendPacket(iq);
        XmppErrorConditionToExceptionTranslator.handleError(response, provider);
        LOGGER.debug(Messages.Log.SUCCESS);
        return null;
    }

    public static IQ marshal(String provider, String federationUpdate) {
        IQ iq = new IQ(IQ.Type.set);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + provider);
        Element queryEl = iq.getElement().addElement(IqElement.QUERY.toString(), RemoteMethod.UPDATE_FEDERATION.toString());

        Element federationUpdateClassNameElement = queryEl.addElement(IqElement.FEDERATION_UPDATE_CLASS_NAME.toString());
        federationUpdateClassNameElement.setText(federationUpdate.getClass().getName());

        Element federationUpdateElement = queryEl.addElement(IqElement.FEDERATION_UPDATE.toString());
        federationUpdateElement.setText(federationUpdate);
        
        return iq;
    }
}
