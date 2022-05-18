package cloud.fogbow.fhs.api.http.request;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

// TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Map.MAP_ENDPOINT)
public class Map {
    public static final String MAP_SUFFIX_ENDPOINT = "Map";
    public static final String MAP_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + MAP_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Map.class);
    
    @RequestMapping(value = "/{federationId}/{serviceId}/{userId}/{cloudName}", method = RequestMethod.GET)
    public ResponseEntity<java.util.Map<String, String>> map(
            @PathVariable String federationId,
            @PathVariable String serviceId,
            @PathVariable String userId,
            @PathVariable String cloudName,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.MAP_RECEIVED);
            java.util.Map<String, String> credentials = ApplicationFacade.getInstance().map(systemUserToken, federationId, 
                    serviceId, userId, cloudName);
            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
