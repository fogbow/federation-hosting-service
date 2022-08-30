package cloud.fogbow.fhs.core.intercomponent.xmpp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.core.PropertiesHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class XmppCommunicationMechanismTest {
    private static final int XMPP_STARTUP_ATTEMPTS = 2;
    private static final String XMPP_STARTUP_ATTEMPTS_STR = String.valueOf(XMPP_STARTUP_ATTEMPTS);
    private static final String XMPP_SERVER_IP = "0.0.0.0";
    private static final String PROVIDER_ID = "providerId";
    private static final String XMPP_PASSWORD = "password";
    private static final int XMPP_C2C_PORT = 10000;
    private static final String XMPP_C2C_PORT_STR = String.valueOf(XMPP_C2C_PORT);
    private static final long XMPP_TIMEOUT = 20000L;
    private static final String XMPP_TIMEOUT_STR = String.valueOf(XMPP_TIMEOUT);
    private static final long XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS = 0L;
    private static final String XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS_STR = String.valueOf(XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS);
    private PropertiesHolder propertiesHolder;
    private XmppPacketSenderInstantiator packetSenderInstantiator;
    private XmppComponentManager packetSender;
    
    @Before
    public void setUp() throws ConfigurationErrorException {
        packetSender = Mockito.mock(XmppComponentManager.class);
        
        packetSenderInstantiator = Mockito.mock(XmppPacketSenderInstantiator.class);
        Mockito.when(packetSenderInstantiator.createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT)).thenReturn(packetSender);
        
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_STARTUP_ATTEMPTS)).thenReturn(XMPP_STARTUP_ATTEMPTS_STR);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS, 
                ConfigurationPropertyDefaults.XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS)).thenReturn(XMPP_TIME_BETWEEN_STARTUP_ATTEMPTS_STR);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY)).thenReturn(XMPP_SERVER_IP);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY)).thenReturn(PROVIDER_ID);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_PASSWORD_KEY)).thenReturn(XMPP_PASSWORD);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_C2C_PORT_KEY, 
                ConfigurationPropertyDefaults.XMPP_CSC_PORT)).thenReturn(XMPP_C2C_PORT_STR);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_TIMEOUT_KEY, 
                ConfigurationPropertyDefaults.XMPP_TIMEOUT)).thenReturn(XMPP_TIMEOUT_STR);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
    }
    
    // test case: When calling the constructor, it must read from the configuration all the required properties
    // correctly and start up the XmppComponentManager, the packet sender, by calling the method createAndConnectPacketSender 
    // of the XmppPacketSenderInstantiator.
    @Test
    public void testConstructor() throws InternalServerErrorException, ConfigurationErrorException {
        new XmppCommunicationMechanism(packetSenderInstantiator);
        
        Mockito.verify(packetSenderInstantiator).createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT);
    }
    
    // test case: When calling the constructor and the packet sender start up fails, it must try again.
    @Test
    public void testConstructorPacketSenderCreationFails() throws ConfigurationErrorException, InternalServerErrorException {
        packetSender = Mockito.mock(XmppComponentManager.class);
        
        packetSenderInstantiator = Mockito.mock(XmppPacketSenderInstantiator.class);
        Mockito.when(packetSenderInstantiator.createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT)).
                thenThrow(new IllegalStateException()).thenReturn(packetSender);

        new XmppCommunicationMechanism(packetSenderInstantiator);
        
        Mockito.verify(packetSenderInstantiator, Mockito.times(2)).createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT);
    }
    
    // test case: When calling the constructor and the packet sender start up fails more times than the limit, it must 
    // throw an InternalServerErrorException.
    @Test
    public void testConstructorPacketSenderCreationFailsMoreThanLimit() throws ConfigurationErrorException, InternalServerErrorException {
        packetSender = Mockito.mock(XmppComponentManager.class);
        
        packetSenderInstantiator = Mockito.mock(XmppPacketSenderInstantiator.class);
        Mockito.when(packetSenderInstantiator.createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT)).
                thenThrow(new IllegalStateException()).thenThrow(new IllegalStateException());

        try {
            new XmppCommunicationMechanism(packetSenderInstantiator);
            Assert.fail("Expected InternalServerErrorException.");
        } catch (InternalServerErrorException e) {
            
        }
        
        Mockito.verify(packetSenderInstantiator, Mockito.times(2)).createAndConnectPacketSender(
                XMPP_SERVER_IP, XMPP_C2C_PORT, PROVIDER_ID, XMPP_PASSWORD, XMPP_TIMEOUT);
    }
    
    // test case: When calling the constructor and the XMPP_SERVER_IP property is null, 
    // it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorFailsIfServerIpIsNull() throws ConfigurationErrorException, InternalServerErrorException {
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY)).thenReturn(null);

        new XmppCommunicationMechanism(packetSenderInstantiator);
    }
    
    // test case: When calling the constructor and the XMPP_SERVER_IP property is empty,
    // it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorFailsIfServerIpIsEmpty() throws ConfigurationErrorException, InternalServerErrorException {
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY)).thenReturn("");

        new XmppCommunicationMechanism(packetSenderInstantiator);
    }
}
