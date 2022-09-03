package cloud.fogbow.fhs.core;

import java.security.interfaces.RSAPublicKey;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PublicKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;

public class FhsPublicKeysHolder {
    private RSAPublicKey asPublicKey;
    private RSAPublicKey rasPublicKey;
    
    private static FhsPublicKeysHolder instance;

    private FhsPublicKeysHolder() {
    }

    public static synchronized FhsPublicKeysHolder getInstance() {
        if (instance == null) {
            instance = new FhsPublicKeysHolder();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }
    
    public RSAPublicKey getAsPublicKey() throws FogbowException {
        if (this.asPublicKey == null) {
            String asAddress = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_URL_KEY);
            String asPort = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_PORT_KEY);
            this.asPublicKey = PublicKeysHolder.getPublicKey(asAddress, asPort, cloud.fogbow.as.api.http.request.PublicKey.PUBLIC_KEY_ENDPOINT);
        }
        return this.asPublicKey;
    }

    public RSAPublicKey getRasPublicKey() throws FogbowException {
        if (this.rasPublicKey == null) {
            String rasAddress = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.RAS_URL_KEY);
            String rasPort = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.RAS_PORT_KEY);
            this.rasPublicKey = PublicKeysHolder.getPublicKey(rasAddress, rasPort, cloud.fogbow.ras.api.http.request.PublicKey.PUBLIC_KEY_ENDPOINT);
        }
        return this.rasPublicKey;
    }
}
