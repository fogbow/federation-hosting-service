package cloud.fogbow.fhs.core.models.invocation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.as.core.util.TokenProtector;
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.CryptoUtil;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.core.FhsPublicKeysHolder;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.SystemToFederationMapperInstantiator;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.SystemToFederationMapper;
import cloud.fogbow.fhs.core.models.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class FogbowServiceInvoker extends HttpServiceInvoker {
    private static final String RESPONSE_CONTENT_KEY = "content";
    
    private RSAPublicKey asPublicKey;
    private RSAPrivateKey asPrivateKey;
    private SystemToFederationMapper mapper;
    private String serviceType;
    private String servicePublicKeyUrl;
    
    public FogbowServiceInvoker(String serializedMetadata) throws IOException, GeneralSecurityException {
        Map<String, String> metadata = new MapUtils().deserializeMap(serializedMetadata);
        // FIXME constant
        this.serviceType = metadata.get("serviceType");
        this.servicePublicKeyUrl = metadata.get("servicePublicKeyUrl");
        String federationName = metadata.get("federationName");
        
        String systemToFederationMapperClassName = 
                PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.SYSTEM_TO_FEDERATION_MAPPER_CLASS_NAME);
        
        this.mapper = new SystemToFederationMapperInstantiator().getPlugin(systemToFederationMapperClassName, federationName);
        
        // TODO check if property exists
        String asPublicKeyFilePath = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_PUBLIC_KEY_FILE_PATH);
        String asPrivateKeyFilePath = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_PRIVATE_KEY_FILE_PATH);
        
        this.asPublicKey = CryptoUtil.getPublicKey(asPublicKeyFilePath);
        this.asPrivateKey = CryptoUtil.getPrivateKey(asPrivateKeyFilePath);
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
        SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, token);

        SystemUser federatedSystemUser = new SystemUser(this.mapper.systemIdToFederationId(systemUser.getId()),
                systemUser.getName(), systemUser.getIdentityProviderId());
        
        RSAPublicKey servicePublicKey = null;
        
        if (this.serviceType.equals("ras")) {
            servicePublicKey = FhsPublicKeysHolder.getInstance().getRasPublicKey();
        } else {
            // FIXME should throw exception
        }
        
        String federatedToken = createFogbowToken(federatedSystemUser, asPrivateKey, servicePublicKey);
        
        headers.put(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY, federatedToken);
        return headers;
    }
    
    private static String createFogbowToken(SystemUser systemUser, RSAPrivateKey privateKey, RSAPublicKey publicKey)
            throws InternalServerErrorException {
        String tokenAttributes = SystemUser.serialize(systemUser);
        String expirationTime = generateExpirationTime();
        String payload = tokenAttributes + FogbowConstants.PAYLOAD_SEPARATOR + expirationTime;
        return encryptToken(payload, privateKey, publicKey);
    }
    
    private static String encryptToken(String token, RSAPrivateKey privateKey, RSAPublicKey publicKey) 
            throws InternalServerErrorException {
        try {
            String signature = CryptoUtil.sign(privateKey, token);
            String signedUnprotectedToken = token + FogbowConstants.TOKEN_SEPARATOR + signature;
            return TokenProtector.encrypt(publicKey, signedUnprotectedToken, FogbowConstants.TOKEN_STRING_SEPARATOR);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            throw new InternalServerErrorException();
        }
    }

    private static String generateExpirationTime() {
        String expirationIntervalProperty = String.valueOf(TimeUnit.DAYS.toMillis(1));
        Long expirationInterval = Long.valueOf(expirationIntervalProperty);
        Date expirationDate = new Date(getNow() + expirationInterval);
        String expirationTime = Long.toString(expirationDate.getTime());
        return expirationTime;
    }

    private static long getNow() {
        return System.currentTimeMillis();
    }
    

    @Override
    Map<String, String> prepareBody(Map<String, String> body) {
        return body;
    }

    @Override
    ServiceResponse prepareResponse(HttpResponse response) {
        Map<String, String> contentMap = new HashMap<String, String>();
        contentMap.put(RESPONSE_CONTENT_KEY, response.getContent());
        return new DefaultServiceResponse(response.getHttpCode(), contentMap);
    }
}
