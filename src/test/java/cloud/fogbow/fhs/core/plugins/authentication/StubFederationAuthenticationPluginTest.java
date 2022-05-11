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

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AuthenticationUtil.class , ServiceAsymmetricKeysHolder.class })
public class StubFederationAuthenticationPluginTest {
    private static final String EXPECTED_TOKEN = "token";
    private static final String LOCAL_PROVIDER_ID = "providerId";
    private static final String PUBLIC_KEY_STRING = "publicKey";
    private static final String USER_ID = "userId";
    
    private StubFederationAuthenticationPlugin plugin;
    private Map<String, String> credentials;
    private RSAPrivateKey privateKey;
    private ServiceAsymmetricKeysHolder keysHolder;
    
    @Test
    public void testAuthenticate() throws UnauthenticatedUserException, ConfigurationErrorException, 
    InternalServerErrorException {
        SystemUser expectedGeneratedSystemUser = new SystemUser(USER_ID, USER_ID, LOCAL_PROVIDER_ID);
        
        credentials = new HashMap<String, String>();
        credentials.put(StubFederationAuthenticationPlugin.USER_PUBLIC_KEY, PUBLIC_KEY_STRING);
        credentials.put(StubFederationAuthenticationPlugin.USER_ID_CREDENTIAL_KEY, USER_ID);
        
        privateKey = Mockito.mock(RSAPrivateKey.class);
        
        keysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(keysHolder.getPrivateKey()).thenReturn(privateKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(keysHolder);
        
        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.createFogbowToken(
                Mockito.eq(expectedGeneratedSystemUser), Mockito.eq(privateKey), Mockito.eq(PUBLIC_KEY_STRING))).
                willReturn(EXPECTED_TOKEN);
        
        this.plugin = new StubFederationAuthenticationPlugin(LOCAL_PROVIDER_ID);
        
        String returnedToken = this.plugin.authenticate(credentials);
        
        assertEquals(EXPECTED_TOKEN, returnedToken);
    }
}
