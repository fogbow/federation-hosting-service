package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;

public class StubFederationAuthenticationPlugin implements FederationAuthenticationPlugin {
    public static final String USER_PUBLIC_KEY = "userPublicKey";
    public static final String USER_ID_CREDENTIAL_KEY = "username";
    
    private String localProviderId;
    
    public StubFederationAuthenticationPlugin(HashMap<String, String> properties) {
        this.localProviderId =
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY);
    }
    
    public StubFederationAuthenticationPlugin(String localProviderId) {
        this.localProviderId = localProviderId;
    }
    
    @Override
    public String authenticate(Map<String, String> credentials)
            throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        String userId = credentials.get(USER_ID_CREDENTIAL_KEY);
        String publicKeyString = credentials.get(USER_PUBLIC_KEY);
        
        SystemUser systemUser = new SystemUser(userId, userId, this.localProviderId);
        return AuthenticationUtil.createFogbowToken(systemUser, 
                ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), publicKeyString);
    }

    @Override
    public SystemUser validateToken(String token) throws UnauthenticatedUserException, InternalServerErrorException {
        return AuthenticationUtil.authenticate(ServiceAsymmetricKeysHolder.getInstance().getPublicKey(), token);
    }
}
