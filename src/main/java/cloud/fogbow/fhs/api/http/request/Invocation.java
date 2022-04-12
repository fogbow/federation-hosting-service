package cloud.fogbow.fhs.api.http.request;

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

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.parameters.RequestData;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Invocation.INVOCATION_ENDPOINT)
public class Invocation {
    public static final String INVOCATION_SUFFIX_ENDPOINT = "Invocation";
    public static final String INVOCATION_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + INVOCATION_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Invocation.class);

    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<RequestResponse> invocationGet(
            @PathVariable String federationId,
            @PathVariable String serviceId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_GET_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.GET, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.POST)
    public ResponseEntity<RequestResponse> invocationPost(
            @PathVariable String federationId,
            @PathVariable String serviceId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_POST_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.POST, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.PUT)
    public ResponseEntity<RequestResponse> invocationPut(
            @PathVariable String federationId,
            @PathVariable String serviceId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_PUT_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.PUT, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.DELETE)
    public ResponseEntity<RequestResponse> invocationDelete(
            @PathVariable String federationId,
            @PathVariable String serviceId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_DELETE_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.DELETE, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
