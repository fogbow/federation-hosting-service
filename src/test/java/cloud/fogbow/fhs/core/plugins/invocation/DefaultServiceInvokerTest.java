package cloud.fogbow.fhs.core.plugins.invocation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpRequestClient.class })
public class DefaultServiceInvokerTest {
    private static final String PATH_1 = "path1";
    private static final String PATH_2 = "path2";
    private static final int RESPONSE_CODE = 200;
    private static final String RESPONSE_CONTENT = "content";
    private static final String ENDPOINT = "endpoint";
    
    private DefaultServiceInvoker serviceInvoker;
    
    @Test
    public void testPreparePathDoesNotChangePath() {
        this.serviceInvoker = new DefaultServiceInvoker();
        
        List<String> pathToPrepare = new ArrayList<>();
        pathToPrepare.add(PATH_1);
        pathToPrepare.add(PATH_2);
        
        assertEquals(pathToPrepare, this.serviceInvoker.preparePath(pathToPrepare));
    }
    
    @Test
    public void testPrepareBodyDoesNotChangeBody() {
        this.serviceInvoker = new DefaultServiceInvoker();
        
        HashMap<String, Object> bodyToPrepare = new HashMap<>();
        bodyToPrepare.put("bodyKey1", "bodyValue1");
        bodyToPrepare.put("bodyKey2", "bodyValue2");
        
        assertEquals(bodyToPrepare, this.serviceInvoker.prepareBody(bodyToPrepare));
    }
    
    @Test
    public void testPrepareHeadersDoesNotChangeHeaders() {
        this.serviceInvoker = new DefaultServiceInvoker();
        
        HashMap<String, String> headersToPrepare = new HashMap<>();
        headersToPrepare.put("headerKey1", "headerValue1");
        headersToPrepare.put("headerKey2", "headerValue2");
        
        assertEquals(headersToPrepare, this.serviceInvoker.prepareHeaders(headersToPrepare));
    }
    
    @Test
    public void testPrepareResponse() {
        this.serviceInvoker = new DefaultServiceInvoker();

        HttpResponse response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.getHttpCode()).thenReturn(RESPONSE_CODE);
        Mockito.when(response.getContent()).thenReturn(RESPONSE_CONTENT);
        
        ServiceResponse preparedResponse = this.serviceInvoker.prepareResponse(response);
        
        assertEquals(RESPONSE_CODE, preparedResponse.getCode());
        Map<String, String> contentMap = preparedResponse.getResponse();
        assertEquals(1, contentMap.size());
        assertEquals(RESPONSE_CONTENT, contentMap.get(DefaultServiceInvoker.RESPONSE_CONTENT_KEY));
    }
    
    @Test
    public void testInvoke() throws FogbowException {
        this.serviceInvoker = new DefaultServiceInvoker();
        
        FederationUser federationUser = Mockito.mock(FederationUser.class);
        List<String> path = Arrays.asList(PATH_1, PATH_2);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("headerKey1", "headerValue1");
        headers.put("headerKey2", "headerValue2");
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("bodyKey1", "bodyValue1");
        body.put("bodyKey2", "bodyValue2");
        
        HttpResponse response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.getHttpCode()).thenReturn(RESPONSE_CODE);
        Mockito.when(response.getContent()).thenReturn(RESPONSE_CONTENT);
        
        PowerMockito.mockStatic(HttpRequestClient.class);
        BDDMockito.given(HttpRequestClient.doGenericRequestGenericBody(
                HttpMethod.GET, ENDPOINT + "/" + PATH_1 + "/" + PATH_2, headers, body)).willReturn(response);
        
        ServiceResponse invocationResponse = this.serviceInvoker.invoke(federationUser, ENDPOINT, HttpMethod.GET, path, headers, body);
        
        assertEquals(RESPONSE_CODE, invocationResponse.getCode());
        Map<String, String> responseContentMap = invocationResponse.getResponse();
        assertEquals(RESPONSE_CONTENT, responseContentMap.get(DefaultServiceInvoker.RESPONSE_CONTENT_KEY));
    }
}
