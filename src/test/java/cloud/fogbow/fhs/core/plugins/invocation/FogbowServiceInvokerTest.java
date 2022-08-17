package cloud.fogbow.fhs.core.plugins.invocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.as.core.util.TokenProtector;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.PublicKeysHolder;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TokenProtector.class, ServiceAsymmetricKeysHolder.class, 
    AuthenticationUtil.class, PublicKeysHolder.class })
public class FogbowServiceInvokerTest {
    private static final String PATH_1 = "path1";
    private static final String PATH_2 = "path2";
    private static final String TOKEN = "token";
    private static final String HEADER_KEY_1 = "headerKey1";
    private static final String HEADER_KEY_2 = "headerKey2";
    private static final String HEADER_VALUE_1 = "headerValue1";
    private static final String HEADER_VALUE_2 = "headerValue2";
    private static final String BODY_KEY_1 = "bodyKey1";
    private static final String BODY_KEY_2 = "bodyKey2";
    private static final String BODY_VALUE_1 = "bodyValue1";
    private static final String BODY_VALUE_2 = "bodyValue2";
    private static final int RESPONSE_CODE = 201;
    private static final String RESPONSE_CONTENT = "content";
    private static final String USER_NAME = "user";
    private static final String FEDERATION_ID = "federation";
    private static final String SERVICE_PUBLIC_KEY_ENDPOINT = "http://0.0.0.0/service/publicKey";
    private static final String SERVICE_ID = "serviceId";
    private static final String LOCAL_PROVIDER_ID = "providerId";
    
    private FogbowServiceInvoker serviceInvoker;
    private Map<String, String> metadata;
    private List<String> path;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private RSAPublicKey rasPublicKey;
    private RSAPrivateKey fhsPrivateKey;
    
    @Before
    public void setUp() throws FogbowException {
        this.path = new ArrayList<String>();
        this.path.add(PATH_1);
        this.path.add(PATH_2);
        
        this.metadata = new HashMap<String, String>();
        this.metadata.put(FogbowServiceInvoker.SERVICE_PUBLIC_KEY_ENDPOINT, SERVICE_PUBLIC_KEY_ENDPOINT);
        
        this.rasPublicKey = Mockito.mock(RSAPublicKey.class);
        this.fhsPrivateKey = Mockito.mock(RSAPrivateKey.class);
        
        PowerMockito.mockStatic(PublicKeysHolder.class);
        BDDMockito.given(PublicKeysHolder.getPublicKey(SERVICE_PUBLIC_KEY_ENDPOINT)).willReturn(rasPublicKey);
        
        ServiceAsymmetricKeysHolder serviceKeysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(serviceKeysHolder.getPrivateKey()).thenReturn(this.fhsPrivateKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(serviceKeysHolder);

        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.createFogbowToken(Mockito.any(SystemUser.class), 
                Mockito.any(RSAPrivateKey.class), Mockito.any(RSAPublicKey.class))).willReturn(TOKEN);
        
        this.headers = new HashMap<String, String>();
        this.headers.put(HEADER_KEY_1, HEADER_VALUE_1);
        this.headers.put(HEADER_KEY_2, HEADER_VALUE_2);
        
        this.body = new HashMap<String, Object>();
        this.body.put(BODY_KEY_1, BODY_VALUE_1);
        this.body.put(BODY_KEY_2, BODY_VALUE_2);
        
        this.serviceInvoker = new FogbowServiceInvoker(this.metadata, LOCAL_PROVIDER_ID);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfServicePublicKeyEndpointIsNull() throws ConfigurationErrorException {
        new FogbowServiceInvoker(new HashMap<String, String>(), LOCAL_PROVIDER_ID);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfServicePublicKeyEndpointIsEmpty() throws ConfigurationErrorException {
        this.metadata.put(FogbowServiceInvoker.SERVICE_PUBLIC_KEY_ENDPOINT, "");
        new FogbowServiceInvoker(this.metadata, LOCAL_PROVIDER_ID);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfLocalProviderIsNull() throws ConfigurationErrorException {
        new FogbowServiceInvoker(this.metadata, null);
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfLocalProviderIsEmpty() throws ConfigurationErrorException {
        new FogbowServiceInvoker(this.metadata, "");
    }
    
    @Test
    public void testPreparePathDoesNotChangePath() throws IOException, GeneralSecurityException {
        int pathSize = this.path.size();
        
        List<String> preparedPath = this.serviceInvoker.preparePath(this.path);
        
        assertEquals(pathSize, preparedPath.size());
        assertTrue(preparedPath.contains(PATH_1));
        assertTrue(preparedPath.contains(PATH_2));
    }
    
    @Test
    public void testPrepareHeaders() throws FogbowException {
        FederationUser user = Mockito.mock(FederationUser.class);
        Mockito.when(user.getName()).thenReturn(USER_NAME);
        Mockito.when(user.getFederationId()).thenReturn(FEDERATION_ID);
        
        Map<String, String> preparedHeaders = this.serviceInvoker.prepareHeaders(headers, user, SERVICE_ID);
        
        assertEquals(3, preparedHeaders.size());
        assertEquals(TOKEN, preparedHeaders.get(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY));
        assertEquals(HEADER_VALUE_1, preparedHeaders.get(HEADER_KEY_1));
        assertEquals(HEADER_VALUE_2, preparedHeaders.get(HEADER_KEY_2));
    }
    
    @Test
    public void testPrepareBodyDoesNotChangeBody() {
        int bodySize = body.size();
        
        Map<String, Object> preparedBody = this.serviceInvoker.prepareBody(body);
        
        assertEquals(bodySize, preparedBody.size());
        assertEquals(BODY_VALUE_1, preparedBody.get(BODY_KEY_1));
        assertEquals(BODY_VALUE_2, preparedBody.get(BODY_KEY_2));
    }
    
    @Test
    public void testPrepareResponse() {
        HttpResponse response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.getHttpCode()).thenReturn(RESPONSE_CODE);
        Mockito.when(response.getContent()).thenReturn(RESPONSE_CONTENT);
        
        ServiceResponse preparedResponse = this.serviceInvoker.prepareResponse(response);
        
        assertEquals(RESPONSE_CODE, preparedResponse.getCode());
        Map<String, String> contentMap = preparedResponse.getResponse();
        assertEquals(1, contentMap.size());
        assertEquals(RESPONSE_CONTENT, contentMap.get(DefaultServiceInvoker.RESPONSE_CONTENT_KEY));
    }
}
