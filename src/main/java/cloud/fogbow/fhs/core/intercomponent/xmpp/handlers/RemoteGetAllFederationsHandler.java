package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class RemoteGetAllFederationsHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteGetAllFederationsHandler.class);
    
    private static final String REMOTE_GET_ALL_FEDERATIONS = RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString();
            
    private JsonUtils jsonUtils;
    
    public RemoteGetAllFederationsHandler() {
        super(RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString());
        this.jsonUtils = new JsonUtils();
    }
    
    public RemoteGetAllFederationsHandler(JsonUtils jsonUtils) {
        super(RemoteMethod.REMOTE_GET_ALL_FEDERATIONS.toString());
        this.jsonUtils = jsonUtils;
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
        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(jsonUtils.toJson(federationInstances));

        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(federationInstances.getClass().getName());
    }
}
