package cloud.fogbow.fhs.core.intercomponent.xmpp;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jamppa.component.XMPPComponent;
import org.xmpp.component.ComponentException;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.RemoteRequest;

public class XMPPCommunicationMechanism implements FhsCommunicationMechanism {
    private final static Logger LOGGER = Logger.getLogger(XMPPCommunicationMechanism.class);
    private static XmppComponentManager packetSender;
    
    public XMPPCommunicationMechanism() throws InternalServerErrorException {
        Integer attempts = Integer.valueOf(
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_STARTUP_ATTEMPTS));
        
        for (int i = 0; i < attempts; i++) {
            try {
                setup();
                LOGGER.info(Messages.Log.PACKET_SENDER_INITIALIZED);
                break;
            } catch (IllegalStateException e1) {
                LOGGER.error(Messages.Log.NO_PACKET_SENDER, e1);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                
                if (i == attempts) {
                    throw new InternalServerErrorException(Messages.Log.CANNOT_INITIALIZE_PACKAGE_SENDER);
                }
            }
        }
        
    }

    public void setup() {
        String jidServiceName = SystemConstants.JID_SERVICE_NAME;
        String jidConnector = SystemConstants.JID_CONNECTOR;
        String jidPrefix = SystemConstants.XMPP_SERVER_NAME_PREFIX;
        String providerId = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY);
        String xmppPassword = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_PASSWORD_KEY);
        String xmppServerIp = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY);
        int xmppServerPort = Integer.parseInt(PropertiesHolder.getInstance().
                getProperty(ConfigurationPropertyKeys.XMPP_C2C_PORT_KEY, ConfigurationPropertyDefaults.XMPP_CSC_PORT));
        long xmppTimeout =
                Long.parseLong(PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_TIMEOUT_KEY,
                        ConfigurationPropertyDefaults.XMPP_TIMEOUT));
        System.out.println(jidServiceName + jidConnector +
                jidPrefix + providerId);
        XmppComponentManager xmppComponentManager = new XmppComponentManager(jidServiceName + jidConnector +
                jidPrefix + providerId, xmppPassword, xmppServerIp, xmppServerPort, xmppTimeout);
        if (xmppServerIp != null && !xmppServerIp.isEmpty()) {
            try {
                LOGGER.info(Messages.Log.CONNECTING_UP_PACKET_SENDER);
                xmppComponentManager.connect();
            } catch (ComponentException e) {
                throw new IllegalStateException();
            }
            XMPPCommunicationMechanism.packetSender = xmppComponentManager;
        } else {
            LOGGER.info(Messages.Log.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
    }
    
    public static class XmppComponentManager extends XMPPComponent {
        private static Logger LOGGER = Logger.getLogger(XmppComponentManager.class);

        public XmppComponentManager(String jid, String password, String xmppServerIp, int xmppServerPort, long timeout) {
            super(jid, password, xmppServerIp, xmppServerPort, timeout);
            // instantiate set handlers here
            // instantiate get handlers here
            LOGGER.info(Messages.Log.XMPP_HANDLERS_SET);
        }
    }

    @Override
    public String sendRequest(RemoteRequest request) {
        // TODO Auto-generated method stub
        return null;
    }
}
