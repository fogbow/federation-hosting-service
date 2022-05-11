package cloud.fogbow.fhs.core.plugins.authentication;

import static org.junit.Assert.assertEquals;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.as.core.systemidp.plugins.ldap.LdapSystemIdentityProviderPlugin;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AuthenticationUtil.class , ServiceAsymmetricKeysHolder.class })
public class LdapFederationAuthenticationPluginTest {
    private static final String EXPECTED_TOKEN = "expectedToken";
    private static final String PUBLIC_KEY_STRING = "publicKey";
    private static final String USER_ID = "userId";
    
    private LdapSystemIdentityProviderPlugin ldapPlugin;
    private LdapFederationAuthenticationPlugin plugin;
    private Map<String, String> credentials;
    private SystemUser systemUser;
    private RSAPrivateKey privateKey;
    private ServiceAsymmetricKeysHolder keysHolder;

    @Test
    public void testAuthenticate() throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException {
        privateKey = Mockito.mock(RSAPrivateKey.class);
        
        keysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(keysHolder.getPrivateKey()).thenReturn(privateKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(keysHolder);
        
        credentials = new HashMap<String, String>();
        credentials.put(StubFederationAuthenticationPlugin.USER_PUBLIC_KEY, PUBLIC_KEY_STRING);
        credentials.put(StubFederationAuthenticationPlugin.USER_ID_CREDENTIAL_KEY, USER_ID);
        
        systemUser = Mockito.mock(SystemUser.class);
        
        ldapPlugin = Mockito.mock(LdapSystemIdentityProviderPlugin.class);
        Mockito.when(ldapPlugin.getSystemUser(credentials)).thenReturn(systemUser);
        
        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.createFogbowToken(
                Mockito.eq(systemUser), Mockito.eq(privateKey), Mockito.eq(PUBLIC_KEY_STRING))).
                willReturn(EXPECTED_TOKEN);
        
        this.plugin = new LdapFederationAuthenticationPlugin(ldapPlugin);

        String returnedToken = this.plugin.authenticate(credentials);
        
        assertEquals(EXPECTED_TOKEN, returnedToken);
    }
}
