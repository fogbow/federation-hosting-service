package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import com.google.gson.Gson;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppComponentManager;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationFactory;
import cloud.fogbow.fhs.core.models.FederationUser;

// TODO test
public class RemoteJoinFederationRequest implements RemoteRequest<Federation> {
    private static final Logger LOGGER = Logger.getLogger(RemoteJoinFederationRequest.class);
    
    private XmppComponentManager packetSender;
    private FederationUser requester;
    private String federationId;
    private String provider;
    private FederationFactory federationFactory;
    
    public RemoteJoinFederationRequest(XmppComponentManager packetSender, FederationUser requester, String federationId,
            String provider) {
        this.packetSender = packetSender;
        this.requester = requester;
        this.federationId = federationId;
        this.provider = provider;
        this.federationFactory = new FederationFactory();
    }

    @Override
    public Federation send() throws FogbowException {
        IQ iq = marshal(new Gson().toJson(requester), federationId, provider);
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) packetSender.syncSendPacket(iq);
        XmppErrorConditionToExceptionTranslator.handleError(response, provider);
        LOGGER.debug(Messages.Log.SUCCESS);
        return unmarshalFederation(response);
    }

    public static IQ marshal(String requester, String federationId, String provider) {
        IQ iq = new IQ(IQ.Type.set);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + provider);
        Element joinEl = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_JOIN_FEDERATION.toString());
        
        Element federationIdEl = joinEl.addElement(IqElement.FEDERATION_ID.toString());
        federationIdEl.setText(federationId);
       
        Element requesterEl = joinEl.addElement(IqElement.REQUESTER_USER.toString());
        requesterEl.setText(requester);
        
        return iq;
    }
    
    private Federation unmarshalFederation(IQ response) throws InternalServerErrorException {
        Element queryElement = response.getElement().element(IqElement.QUERY.toString());
        String federationStr = queryElement.element(IqElement.REMOTE_FEDERATION.toString()).getText();
        return this.federationFactory.createFederation(federationStr);
    }
}
