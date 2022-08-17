package cloud.fogbow.fhs.core.intercomponent.xmpp.handlers;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.gson.reflect.TypeToken;

import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.RemoteFacade;
import cloud.fogbow.fhs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.fhs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.fhs.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

public class RemoteUpdateFederationHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteSyncFederationsHandler.class);
    
    private JsonUtils jsonUtils;
    
    public RemoteUpdateFederationHandler(JsonUtils jsonUtils) {
        super(RemoteMethod.UPDATE_FEDERATION.toString());
        this.jsonUtils = jsonUtils;
    }
    
    public RemoteUpdateFederationHandler() {
        super(RemoteMethod.UPDATE_FEDERATION.toString());
        this.jsonUtils = new JsonUtils();
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));

        IQ response = IQ.createResultIQ(iq);
        try {
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            
            FederationUpdate federationUpdate = unmarshalFederationUpdate(iq);
            RemoteFacade.getInstance().updateFederation(senderId, federationUpdate);
        } catch (Throwable e) {
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }

        return response;
    }

    private FederationUpdate unmarshalFederationUpdate(IQ request) {
        Element queryElement = request.getElement().element(IqElement.QUERY.toString());
        String updateStr = queryElement.element(IqElement.FEDERATION_UPDATE.toString()).getText();
        return this.jsonUtils.fromJson(updateStr, new TypeToken<FederationUpdate>(){});
    }
}
