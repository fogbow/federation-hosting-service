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
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Invocation.INVOCATION_ENDPOINT)
@Api(description = ApiDocumentation.Invocation.API)
public class Invocation {
    public static final String INVOCATION_SUFFIX_ENDPOINT = "Invocation";
    public static final String INVOCATION_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + INVOCATION_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Invocation.class);

    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_GET)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<RequestResponse> invocationGet(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
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
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_POST)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.POST)
    public ResponseEntity<RequestResponse> invocationPost(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
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
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_PUT)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.PUT)
    public ResponseEntity<RequestResponse> invocationPut(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
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
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_DELETE)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.DELETE)
    public ResponseEntity<RequestResponse> invocationDelete(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
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
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_PATCH)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.PATCH)
    public ResponseEntity<RequestResponse> invocationPatch(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_PATCH_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.PATCH, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_OPTIONS)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<RequestResponse> invocationOptions(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_OPTIONS_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.OPTIONS, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Invocation.INVOKE_HEAD)
    @RequestMapping(value = "/{federationId}/{serviceId}", method = RequestMethod.HEAD)
    public ResponseEntity<RequestResponse> invocationHead(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Invocation.INVOCATION_BODY)
            @RequestBody RequestData requestData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.INVOKE_HEAD_REQUEST_RECEIVED);
            RequestResponse response = ApplicationFacade.getInstance().invocation(systemUserToken, federationId, serviceId, HttpMethod.HEAD, 
                    requestData.getPath(), requestData.getHeaders(), requestData.getBody());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
