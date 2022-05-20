package cloud.fogbow.fhs.api.http.request;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.api.http.response.AttributeDescription;
import cloud.fogbow.fhs.api.parameters.AttributeSpec;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Attributes.ATTRIBUTES_ENDPOINT)
public class Attributes {
    public static final String ATTRIBUTES_SUFFIX_ENDPOINT = "Attributes";
    public static final String ATTRIBUTES_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + ATTRIBUTES_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Attributes.class);
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.POST)
    public ResponseEntity<AttributeDescription> createAttribute(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody AttributeSpec attributeSpec) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.CREATE_FEDERATION_ATTRIBUTE_RECEIVED);
            AttributeDescription attributeDescription = ApplicationFacade.getInstance().createAttribute(systemUserToken,  
                    federationId, attributeSpec.getName());
            return new ResponseEntity<>(attributeDescription, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.GET)
    public ResponseEntity<List<AttributeDescription>> getAttributes(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_FEDERATION_ATTRIBUTES_RECEIVED);
            List<AttributeDescription> federationAttributes = ApplicationFacade.getInstance().
                    getFederationAttributes(systemUserToken, federationId);
            return new ResponseEntity<>(federationAttributes, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}/{attributeId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteAttribute(
            @PathVariable String federationId,
            @PathVariable String attributeId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.DELETE_FEDERATION_ATTRIBUTE_RECEIVED);
            ApplicationFacade.getInstance().deleteFederationAttribute(systemUserToken, federationId, attributeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
