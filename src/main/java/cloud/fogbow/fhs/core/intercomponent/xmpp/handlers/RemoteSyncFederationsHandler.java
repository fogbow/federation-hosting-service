package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.gson.Gson;
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

// TODO test
public class RemoteSyncFederationsHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteSyncFederationsHandler.class);
    
    private static final String REMOTE_SYNC_FEDERATIONS = RemoteMethod.SYNC_FEDERATIONS.toString();
            
    public RemoteSyncFederationsHandler() {
        super(RemoteMethod.SYNC_FEDERATIONS.toString());
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

        Element federationListClassNameElement = queryEl.addElement(IqElement.FEDERATION_LIST_CLASS_NAME.toString());
        federationListClassNameElement.setText(localFederations.getClass().getName());

        Element federationListElement = queryEl.addElement(IqElement.FEDERATION_LIST.toString());
        federationListElement.setText(new Gson().toJson(localFederations));
    }
    
    private List<FederationInstance> unmarshalFederationList(IQ request) throws InternalServerErrorException {
        Element queryElement = request.getElement().element(IqElement.QUERY.toString());
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
