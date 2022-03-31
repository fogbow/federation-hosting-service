package cloud.fogbow.fhs.core.models.invocation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.as.core.util.TokenProtector;
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.core.FhsPublicKeysHolder;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class FogbowServiceInvoker extends HttpServiceInvoker {
    private static final String RESPONSE_CONTENT_KEY = "content";
    private String serviceType;
    
    public FogbowServiceInvoker(String serializedMetadata) throws IOException, GeneralSecurityException {
        Map<String, String> metadata = new MapUtils().deserializeMap(serializedMetadata);
        // FIXME constant
        this.serviceType = metadata.get("serviceType");
    }
    
    @Override
    public String getName() {
        return "FogbowServiceInvoker";
    }

    @Override
    List<String> preparePath(List<String> path) {
        return path;
    }

    @Override
    Map<String, String> prepareHeaders(Map<String, String> headers) throws FogbowException {
        String token = headers.get(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY);
        RSAPublicKey servicePublicKey = null;
        
        if (this.serviceType.equals("ras")) {
            servicePublicKey = FhsPublicKeysHolder.getInstance().getRasPublicKey();
        } else {
            // FIXME should throw exception
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
