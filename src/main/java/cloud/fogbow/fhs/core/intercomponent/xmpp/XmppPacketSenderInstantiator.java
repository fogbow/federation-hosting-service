package cloud.fogbow.fhs.core.intercomponent.xmpp;

import org.apache.log4j.Logger;
import org.xmpp.component.ComponentException;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;

public class XmppPacketSenderInstantiator {
    private final static Logger LOGGER = Logger.getLogger(XmppPacketSenderInstantiator.class);
    
    public XmppComponentManager createAndConnectPacketSender(String xmppServerIp, int xmppServerPort, 
            String providerId, String xmppPassword, long xmppTimeout) throws ConfigurationErrorException {
        XmppComponentManager xmppComponentManager = createPacketSender(xmppServerIp, xmppServerPort, providerId,
                xmppPassword, xmppTimeout);
        tryToConnect(xmppComponentManager);
        return xmppComponentManager;
    }

    private XmppComponentManager createPacketSender(String xmppServerIp, int xmppServerPort, String providerId, String xmppPassword, 
            long xmppTimeout) {
        String jidServiceName = SystemConstants.JID_SERVICE_NAME;
        String jidConnector = SystemConstants.JID_CONNECTOR;
        String jidPrefix = SystemConstants.XMPP_SERVER_NAME_PREFIX;
        return new XmppComponentManager(jidServiceName + jidConnector +
                jidPrefix + providerId, xmppPassword, xmppServerIp, xmppServerPort, xmppTimeout);
    }
    
    private void tryToConnect(XmppComponentManager xmppComponentManager) {
        try {
            LOGGER.info(Messages.Log.CONNECTING_UP_PACKET_SENDER);
            xmppComponentManager.connect();
        } catch (ComponentException e) {
            throw new IllegalStateException();
        }
    }
}
