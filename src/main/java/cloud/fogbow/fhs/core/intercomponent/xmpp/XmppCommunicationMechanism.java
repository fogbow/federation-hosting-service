package cloud.fogbow.fhs.core.intercomponent.xmpp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteGetAllFederationsRequest;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteJoinFederationRequest;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.RemoteSyncFederationsRequest;
import cloud.fogbow.fhs.core.intercomponent.xmpp.requesters.UpdateFederationRequest;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

public class XmppCommunicationMechanism implements FhsCommunicationMechanism {
    private final static Logger LOGGER = Logger.getLogger(XmppCommunicationMechanism.class);
    
    private XmppComponentManager packetSender;
    
    public XmppCommunicationMechanism() throws InternalServerErrorException, ConfigurationErrorException {
        this(new XmppPacketSenderInstantiator());
    }
    
    public XmppCommunicationMechanism(XmppPacketSenderInstantiator packetSenderInstantiator) throws InternalServerErrorException, ConfigurationErrorException {
        PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();
        
        Integer attempts = Integer.valueOf(propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_STARTUP_ATTEMPTS));
        Long waitTimeBetweenAttempts = Long.valueOf(propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS, 
                        ConfigurationPropertyDefaults.XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS));
        String xmppServerIp = propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY);
        String providerId = propertiesHolder.getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY);
        String xmppPassword = propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_PASSWORD_KEY);
        
        int xmppServerPort = Integer.parseInt(propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_C2C_PORT_KEY, 
                ConfigurationPropertyDefaults.XMPP_CSC_PORT));
        long xmppTimeout = Long.parseLong(propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_TIMEOUT_KEY, 
                ConfigurationPropertyDefaults.XMPP_TIMEOUT));
        
        if (xmppServerIp == null || xmppServerIp.isEmpty()) {
            LOGGER.info(Messages.Log.NO_REMOTE_COMMUNICATION_CONFIGURED);
            throw new ConfigurationErrorException(Messages.Exception.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
        
        tryToCreatePacketSender(packetSenderInstantiator, attempts, waitTimeBetweenAttempts, xmppServerIp, providerId,
                xmppPassword, xmppServerPort, xmppTimeout);
    }

    private void tryToCreatePacketSender(XmppPacketSenderInstantiator packetSenderInstantiator, Integer attempts,
            Long waitTimeBetweenAttempts, String xmppServerIp, String providerId, String xmppPassword,
            int xmppServerPort, long xmppTimeout) throws ConfigurationErrorException, InternalServerErrorException {
        int i = 0;
        
        for (; i < attempts; i++) {
            try {
                this.packetSender = packetSenderInstantiator.createAndConnectPacketSender(xmppServerIp, xmppServerPort, 
                        providerId, xmppPassword, xmppTimeout);
                LOGGER.info(Messages.Log.PACKET_SENDER_INITIALIZED);
                break;
            } catch (IllegalStateException e1) {
                LOGGER.error(Messages.Log.NO_PACKET_SENDER, e1);
                try {
                    TimeUnit.SECONDS.sleep(waitTimeBetweenAttempts);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
        
        if (i == attempts) {
            throw new InternalServerErrorException(Messages.Log.CANNOT_INITIALIZE_PACKAGE_SENDER);
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

    @Override
    public Federation joinRemoteFederation(FederationUser requester, String federationId, String ownerFhsId) throws FogbowException {
        return new RemoteJoinFederationRequest(packetSender, requester, federationId, ownerFhsId).send();
    }

    @Override
    public void updateFederation(String remoteHost, FederationUpdate update) throws FogbowException {
        new UpdateFederationRequest(packetSender, remoteHost, update).send();
    }
}
