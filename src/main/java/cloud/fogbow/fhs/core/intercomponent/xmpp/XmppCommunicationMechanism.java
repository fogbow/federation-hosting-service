package cloud.fogbow.fhs.core.intercomponent.xmpp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.xmpp.component.ComponentException;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteGetAllFederationsRequest;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteSyncFederationsRequest;

// TODO test
public class XmppCommunicationMechanism implements FhsCommunicationMechanism {
    private final static Logger LOGGER = Logger.getLogger(XmppCommunicationMechanism.class);
    
    private XmppComponentManager packetSender;
    
    public XmppCommunicationMechanism() throws InternalServerErrorException {
        Integer attempts = Integer.valueOf(
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_STARTUP_ATTEMPTS));
        
        for (int i = 0; i < attempts; i++) {
            try {
                setupPacketSender();
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

    private void setupPacketSender() {
        String xmppServerIp = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY);
        
        if (xmppServerIp != null && !xmppServerIp.isEmpty()) {
            XmppComponentManager xmppComponentManager = createPacketSender(xmppServerIp);
            tryToConnect(xmppComponentManager);
            this.packetSender = xmppComponentManager;
        } else {
            LOGGER.info(Messages.Log.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
    }

    private XmppComponentManager createPacketSender(String xmppServerIp) {
        String jidServiceName = SystemConstants.JID_SERVICE_NAME;
        String jidConnector = SystemConstants.JID_CONNECTOR;
        String jidPrefix = SystemConstants.XMPP_SERVER_NAME_PREFIX;
        String providerId = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY);
        String xmppPassword = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_PASSWORD_KEY);
        int xmppServerPort = Integer.parseInt(PropertiesHolder.getInstance().
                getProperty(ConfigurationPropertyKeys.XMPP_C2C_PORT_KEY, ConfigurationPropertyDefaults.XMPP_CSC_PORT));
        long xmppTimeout =
                Long.parseLong(PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_TIMEOUT_KEY,
                        ConfigurationPropertyDefaults.XMPP_TIMEOUT));
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

    @Override
    public List<FederationInstance> getRemoteFederations(String hostId) throws FogbowException {
        return new RemoteGetAllFederationsRequest(packetSender, hostId).send();
    }

    @Override
    public List<FederationInstance> syncFederations(String hostId, List<FederationInstance> localFederations)
            throws FogbowException {
        return new RemoteSyncFederationsRequest(packetSender, hostId, localFederations).send();
    }
}
