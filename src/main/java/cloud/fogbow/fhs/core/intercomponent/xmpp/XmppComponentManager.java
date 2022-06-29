package cloud.fogbow.fhs.core.intercomponent.xmpp;

import org.apache.log4j.Logger;
import org.jamppa.component.XMPPComponent;

import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.intercomponent.xmpp.handlers.RemoteGetAllFederationsHandler;
import cloud.fogbow.fhs.core.intercomponent.xmpp.handlers.RemoteJoinFederationHandler;
import cloud.fogbow.fhs.core.intercomponent.xmpp.handlers.RemoteSyncFederationsHandler;

public class XmppComponentManager extends XMPPComponent {
    private static Logger LOGGER = Logger.getLogger(XmppComponentManager.class);

    public XmppComponentManager(String jid, String password, String xmppServerIp, int xmppServerPort, long timeout) {
        super(jid, password, xmppServerIp, xmppServerPort, timeout);
        // instantiate set handlers here
        addSetHandler(new RemoteJoinFederationHandler());
        // instantiate get handlers here
        addGetHandler(new RemoteGetAllFederationsHandler());
        addGetHandler(new RemoteSyncFederationsHandler());
        LOGGER.info(Messages.Log.XMPP_HANDLERS_SET);
    }
}