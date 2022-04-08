package cloud.fogbow.fhs.core.plugins.invocation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.as.core.util.TokenProtector;
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.core.FhsPublicKeysHolder;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class FogbowServiceInvoker extends HttpServiceInvoker {
    private static final String INVOKER_NAME = "FogbowServiceInvoker";
    public static final String SERVICE_TYPE_KEY = "serviceType";
    public static final String SERVICE_TYPE_RAS = "ras";
    private static final List<String> SERVICE_TYPES = Arrays.asList(SERVICE_TYPE_RAS);
    private static final String RESPONSE_CONTENT_KEY = "content";
    private String serviceType;
    
    public FogbowServiceInvoker(Map<String, String> metadata) throws ConfigurationErrorException {
        this.serviceType = metadata.get(SERVICE_TYPE_KEY);
        
        if (!SERVICE_TYPES.contains(this.serviceType)) {
            // TODO add message
            throw new ConfigurationErrorException();
        }
    }
    
    public FogbowServiceInvoker(String serializedMetadata) 
            throws IOException, GeneralSecurityException, ConfigurationErrorException {
        this(new MapUtils().deserializeMap(serializedMetadata));
    }
    
    @Override
    public String getName() {
        return INVOKER_NAME;
    }

    @Override
    List<String> preparePath(List<String> path) {
        return path;
    }

    @Override
    Map<String, String> prepareHeaders(Map<String, String> headers) throws FogbowException {
        String token = headers.get(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY);
        RSAPublicKey servicePublicKey = null;
        
        if (this.serviceType.equals(SERVICE_TYPE_RAS)) {
            servicePublicKey = FhsPublicKeysHolder.getInstance().getRasPublicKey();
        }
        
        String rewrapToken = TokenProtector.rewrap(ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), 
                servicePublicKey, token, FogbowConstants.TOKEN_STRING_SEPARATOR);
        headers.put(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY, rewrapToken);
        
        return headers;
    }

    @Override
    Map<String, Object> prepareBody(Map<String, Object> body) {
        return body;
    }

    @Override
    ServiceResponse prepareResponse(HttpResponse response) {
        Map<String, String> contentMap = new HashMap<String, String>();
        contentMap.put(RESPONSE_CONTENT_KEY, response.getContent());
        return new DefaultServiceResponse(response.getHttpCode(), contentMap);
    }
}
