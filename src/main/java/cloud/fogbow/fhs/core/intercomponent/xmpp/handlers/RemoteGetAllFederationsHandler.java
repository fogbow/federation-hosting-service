package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.gson.Gson;

import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;

// TODO test
public class RemoteGetAllFederationsHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteGetAllFederationsHandler.class);
    
    private static final String REMOTE_GET_ALL_FEDERATIONS = RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString();
            
    public RemoteGetAllFederationsHandler() {
        super(RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString());
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));

        IQ response = IQ.createResultIQ(iq);
        try {
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            List<FederationInstance> federationInstances = RemoteFacade.getInstance().getFederationList(senderId);
            updateResponse(response, federationInstances);
        } catch (Throwable e) {
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }

        return response;
    }

    private void updateResponse(IQ response, List<FederationInstance> federationInstances) {
        Element queryEl = response.getElement().addElement(IqElement.QUERY.toString(), REMOTE_GET_ALL_FEDERATIONS);
        Element securityRuleListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());

        Element imagesMapClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        imagesMapClassNameElement.setText(federationInstances.getClass().getName());

        securityRuleListElement.setText(new Gson().toJson(federationInstances));
    }
}
