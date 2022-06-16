package cloud.fogbow.fhs.core.intercomponent.xmpp;

import org.apache.log4j.Logger;
import org.jamppa.component.XMPPComponent;

import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.intercomponent.xmpp.handlers.RemoteGetAllFederationsHandler;

public class XmppComponentManager extends XMPPComponent {
    private static Logger LOGGER = Logger.getLogger(XmppComponentManager.class);

    public XmppComponentManager(String jid, String password, String xmppServerIp, int xmppServerPort, long timeout) {
        super(jid, password, xmppServerIp, xmppServerPort, timeout);
        // instantiate set handlers here
        // instantiate get handlers here
        addGetHandler(new RemoteGetAllFederationsHandler());
        LOGGER.info(Messages.Log.XMPP_HANDLERS_SET);
    }
}