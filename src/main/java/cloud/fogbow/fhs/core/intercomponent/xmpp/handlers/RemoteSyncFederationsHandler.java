package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.gson.reflect.TypeToken;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class RemoteSyncFederationsHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteSyncFederationsHandler.class);
    private static final String REMOTE_SYNC_FEDERATIONS = RemoteMethod.SYNC_FEDERATIONS.toString();
            
    private JsonUtils jsonUtils;
    
    public RemoteSyncFederationsHandler(JsonUtils jsonUtils) {
        super(RemoteMethod.SYNC_FEDERATIONS.toString());
        this.jsonUtils = jsonUtils;
    }
    
    public RemoteSyncFederationsHandler() {
        super(RemoteMethod.SYNC_FEDERATIONS.toString());
        this.jsonUtils = new JsonUtils();
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));

        IQ response = IQ.createResultIQ(iq);
        try {
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            
            List<FederationInstance> remoteFederations = unmarshalFederationList(iq);
            RemoteFacade.getInstance().updateRemoteFederationList(senderId, remoteFederations);
            
            List<FederationInstance> federationInstances = RemoteFacade.getInstance().getFederationList(senderId);
            updateResponse(response, federationInstances);
        } catch (Throwable e) {
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }

        return response;
    }

    private void updateResponse(IQ response, List<FederationInstance> localFederations) {
        Element queryEl = response.getElement().addElement(IqElement.QUERY.toString(), REMOTE_SYNC_FEDERATIONS);

        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(this.jsonUtils.toJson(localFederations));
        
        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(localFederations.getClass().getName());
    }
    
    private List<FederationInstance> unmarshalFederationList(IQ request) throws InternalServerErrorException {
        Element queryElement = request.getElement().element(IqElement.QUERY.toString());
        String listStr = queryElement.element(IqElement.FEDERATION_LIST.toString()).getText();

        List<FederationInstance> rulesList;
        try {
            rulesList = this.jsonUtils.fromJson(listStr, new TypeToken<List<FederationInstance>>(){});
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        return rulesList;
    }
}
