package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.gson.Gson;

import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

public class RemoteJoinFederationHandler extends AbstractQueryHandler  {
    private static final Logger LOGGER = Logger.getLogger(RemoteJoinFederationHandler.class);

    private static final String REMOTE_JOIN_FEDERATION = RemoteMethod.REMOTE_JOIN_FEDERATION.toString();
    
    public RemoteJoinFederationHandler() {
        super(REMOTE_JOIN_FEDERATION);
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));

        IQ response = IQ.createResultIQ(iq);
        try {
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
            String federationId = queryElement.element(IqElement.FEDERATION_ID.toString()).getText();
            String requesterStr = queryElement.element(IqElement.REQUESTER_USER.toString()).getText();
            FederationUser requester = new Gson().fromJson(requesterStr, FederationUser.class);
            Federation federation = RemoteFacade.getInstance().joinFederation(senderId, requester, federationId);
            updateResponse(response, federation);
        } catch (Throwable e) {
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }

        return response;
    }

    private void updateResponse(IQ response, Federation federation) {
        Element queryEl = response.getElement().addElement(IqElement.QUERY.toString(), REMOTE_JOIN_FEDERATION);
        Element federationElement = queryEl.addElement(IqElement.REMOTE_FEDERATION.toString());
        federationElement.setText(Federation.toJson(federation));
    }    
}
