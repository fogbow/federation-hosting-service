package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cloud.fogbow.as.constants.ConfigurationPropertyKeys;
import cloud.fogbow.as.core.systemidp.plugins.ldap.LdapSystemIdentityProviderPlugin;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;

public class LdapFederationAuthenticationPlugin implements FederationAuthenticationPlugin {
    private static final Logger LOGGER = Logger.getLogger(LdapFederationAuthenticationPlugin.class);

    private String identityProviderId;
    private String ldapBase;
    private String ldapUrl;
    private String encryptType;
    private LdapSystemIdentityProviderPlugin ldapPlugin;
    
    public LdapFederationAuthenticationPlugin(HashMap<String, String> properties) {
        // FIXME provider id should be the local provider
        ldapPlugin = new LdapSystemIdentityProviderPlugin(
                properties.get(ConfigurationPropertyKeys.PROVIDER_ID_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_BASE_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_URL_KEY), 
                properties.get(ConfigurationPropertyKeys.LDAP_ENCRYPT_TYPE_KEY));
        LOGGER.debug(String.format("p_id=[%s], base=[%s], url=[%s], encry=[%s]", 
                identityProviderId, ldapBase, ldapUrl, encryptType));
    }

    // FIXME should add user attributes
    @Override
    public String authenticate(Map<String, String> credentials) 
            throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        SystemUser systemUser = this.ldapPlugin.getSystemUser(credentials);
        // FIXME constant
        String publicKeyString = credentials.get("userPublicKey");
        return AuthenticationUtil.createFogbowToken(systemUser, 
                ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), publicKeyString);
    }

    @Override
    public SystemUser validateToken(String token) throws UnauthenticatedUserException, InternalServerErrorException {
        return AuthenticationUtil.authenticate(ServiceAsymmetricKeysHolder.getInstance().getPublicKey(), token);
    }
}
