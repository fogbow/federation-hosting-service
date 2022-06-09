package cloud.fogbow.fhs.core.plugins.invocation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.PublicKeysHolder;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class FogbowServiceInvoker extends HttpServiceInvoker {
    private static final String INVOKER_NAME = "FogbowServiceInvoker";
    private static final String RESPONSE_CONTENT_KEY = "content";
    public static final String SERVICE_PUBLIC_KEY_ENDPOINT = "servicePublicKeyEndpoint";
    
    private String localProviderId;
    private String servicePublicKeyEndpoint;
    
    public FogbowServiceInvoker(Map<String, String> metadata, String localProviderId) throws ConfigurationErrorException {
        String servicePublicKeyEndpoint = metadata.get(SERVICE_PUBLIC_KEY_ENDPOINT);
        
        // TODO test
        if (servicePublicKeyEndpoint == null || servicePublicKeyEndpoint.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.INVALID_SERVICE_PUBLIC_KEY_ENDPOINT);
        }
        
        if (localProviderId == null || localProviderId.isEmpty()) {
            throw new ConfigurationErrorException(Messages.Exception.INVALID_LOCAL_PROVIDER_ID);
        }
        
        this.servicePublicKeyEndpoint = servicePublicKeyEndpoint;
        this.localProviderId = localProviderId;
    }
    
    public FogbowServiceInvoker(String serializedMetadata) 
            throws IOException, GeneralSecurityException, ConfigurationErrorException {
        this(new MapUtils().deserializeMap(serializedMetadata),
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY));
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
    Map<String, String> prepareHeaders(Map<String, String> headers, FederationUser user, String serviceId) throws FogbowException {
        SystemUser systemUser = new SystemUser(
                String.join(FogbowConstants.FEDERATION_ID_SEPARATOR, user.getName(), user.getFederationId()), 
                user.getName(), this.localProviderId);
        HashMap<String, String> userMetadata = new HashMap<String, String>();
        // FIXME constant
        userMetadata.put("serviceId", serviceId);
        systemUser.setMetadata(userMetadata);
        
        RSAPublicKey servicePublicKey = PublicKeysHolder.getPublicKey(this.servicePublicKeyEndpoint);
        String userToken = AuthenticationUtil.createFogbowToken(systemUser, 
                ServiceAsymmetricKeysHolder.getInstance().getPrivateKey(), servicePublicKey);
        headers.put(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY, userToken);
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
