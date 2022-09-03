package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.as.core.systemidp.plugins.ldap.LdapSystemIdentityProviderPlugin;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;

public class LdapFederationAuthenticationPlugin implements FederationAuthenticationPlugin {
    public static final String USER_PUBLIC_KEY = "userPublicKey";
    
    private LdapSystemIdentityProviderPlugin ldapPlugin;
    
    public LdapFederationAuthenticationPlugin(HashMap<String, String> properties) {
        ldapPlugin = new LdapSystemIdentityProviderPlugin(
                properties.get(ConfigurationPropertyKeys.PROVIDER_ID_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_BASE_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_URL_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_ENCRYPT_TYPE_KEY));
    }
    
    public LdapFederationAuthenticationPlugin(LdapSystemIdentityProviderPlugin ldapPlugin) {
        this.ldapPlugin = ldapPlugin;
    }

    @Override
    public String authenticate(Map<String, String> credentials) 
            throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        SystemUser systemUser = this.ldapPlugin.getSystemUser(credentials);
        String publicKeyString = credentials.get(USER_PUBLIC_KEY);
        return AuthenticationUtil.createFogbowToken(systemUser, 
                ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), publicKeyString);
    }

    @Override
    public SystemUser validateToken(String token) throws UnauthenticatedUserException, InternalServerErrorException {
        return AuthenticationUtil.authenticate(ServiceAsymmetricKeysHolder.getInstance().getPublicKey(), token);
    }
}
