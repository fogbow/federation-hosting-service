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
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.core.FhsPublicKeysHolder;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FhsPublicKeysHolder.class, TokenProtector.class, 
    ServiceAsymmetricKeysHolder.class })
public class FogbowServiceInvokerTest {
    private static final String PATH_1 = "path1";
    private static final String PATH_2 = "path2";
    private static final String TOKEN = "token";
    private static final String REWRAP_TOKEN = "rewrapToken";
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
    private FogbowServiceInvoker serviceInvoker;
    private Map<String, String> metadata;
    private List<String> path;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private String serviceType;
    private RSAPublicKey rasPublicKey;
    private RSAPrivateKey fhsPrivateKey;
    
    @Before
    public void setUp() throws FogbowException {
        this.serviceType = "ras";
        
        this.path = new ArrayList<String>();
        this.path.add(PATH_1);
        this.path.add(PATH_2);
        
        this.metadata = new HashMap<String, String>();
        this.metadata.put(FogbowServiceInvoker.SERVICE_TYPE_KEY, this.serviceType);
        
        this.rasPublicKey = Mockito.mock(RSAPublicKey.class);
        this.fhsPrivateKey = Mockito.mock(RSAPrivateKey.class);
        
        FhsPublicKeysHolder publicKeysHolder = Mockito.mock(FhsPublicKeysHolder.class);
        Mockito.when(publicKeysHolder.getRasPublicKey()).thenReturn(this.rasPublicKey);
        
        PowerMockito.mockStatic(FhsPublicKeysHolder.class);
        BDDMockito.given(FhsPublicKeysHolder.getInstance()).willReturn(publicKeysHolder);
        
        ServiceAsymmetricKeysHolder serviceKeysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(serviceKeysHolder.getPrivateKey()).thenReturn(this.fhsPrivateKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(serviceKeysHolder);
        
        PowerMockito.mockStatic(TokenProtector.class);
        BDDMockito.given(TokenProtector.rewrap(
                fhsPrivateKey, rasPublicKey, TOKEN, FogbowConstants.TOKEN_STRING_SEPARATOR)).willReturn(REWRAP_TOKEN);
        
        this.headers = new HashMap<String, String>();
        this.headers.put(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY, TOKEN);
        this.headers.put(HEADER_KEY_1, HEADER_VALUE_1);
        this.headers.put(HEADER_KEY_2, HEADER_VALUE_2);
        
        this.body = new HashMap<String, Object>();
        this.body.put(BODY_KEY_1, BODY_VALUE_1);
        this.body.put(BODY_KEY_2, BODY_VALUE_2);
        
        this.serviceInvoker = new FogbowServiceInvoker(this.metadata, null);
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
        int headersSize = this.headers.size();
        
        Map<String, String> preparedHeaders = this.serviceInvoker.prepareHeaders(headers, null);
        
        assertEquals(headersSize, preparedHeaders.size());
        assertEquals(REWRAP_TOKEN, preparedHeaders.get(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY));
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
