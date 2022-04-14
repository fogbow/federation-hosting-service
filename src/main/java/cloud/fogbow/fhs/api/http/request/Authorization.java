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

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Authorization.AUTHORIZATION_ENDPOINT)
public class Authorization {
    public static final String AUTHORIZATION_SUFFIX_ENDPOINT = "Authorization";
    public static final String AUTHORIZATION_ENDPOINT = 
            SystemConstants.SERVICE_BASE_ENDPOINT + AUTHORIZATION_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Authorization.class);
    
    @RequestMapping(value = "/{federationId}/{memberId}/{attributeId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> grantAttribute(
            @PathVariable String federationId,
            @PathVariable String memberId,
            @PathVariable String attributeId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GRANT_FEDERATION_ATTRIBUTE_RECEIVED);
            ApplicationFacade.getInstance().grantAttribute(systemUserToken, federationId, memberId, attributeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}/{memberId}/{attributeId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> revokeAttribute(
            @PathVariable String federationId,
            @PathVariable String memberId,
            @PathVariable String attributeId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.REVOKE_FEDERATION_ATTRIBUTE_RECEIVED);
            ApplicationFacade.getInstance().revokeAttribute(systemUserToken, federationId, memberId, attributeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
