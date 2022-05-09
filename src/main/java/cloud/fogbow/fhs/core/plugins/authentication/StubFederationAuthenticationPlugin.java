package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;

public class StubFederationAuthenticationPlugin implements FederationAuthenticationPlugin {

    public StubFederationAuthenticationPlugin(HashMap<String, String> properties) {
 
    }
    
    @Override
    public String authenticate(Map<String, String> credentials)
            throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        // FIXME constant
        String userId = credentials.get("username");
        
        // FIXME should use local provider
        SystemUser systemUser = new SystemUser(userId, userId, "stub");
        String publicKeyString = credentials.get("userPublicKey");
        return AuthenticationUtil.createFogbowToken(systemUser, 
                ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), publicKeyString);
    }

    @Override
    public SystemUser validateToken(String token) throws UnauthenticatedUserException, InternalServerErrorException {
        return AuthenticationUtil.authenticate(ServiceAsymmetricKeysHolder.getInstance().getPublicKey(), token);
    }
}
