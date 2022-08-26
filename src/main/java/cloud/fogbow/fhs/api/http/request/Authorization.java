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

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.api.http.response.Authorized;
import cloud.fogbow.fhs.api.parameters.OperationToAuthorize;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Authorization.AUTHORIZATION_ENDPOINT)
@Api(description = ApiDocumentation.Authorization.API)
public class Authorization {
    public static final String AUTHORIZATION_SUFFIX_ENDPOINT = "Authorization";
    public static final String AUTHORIZATION_ENDPOINT = 
            SystemConstants.SERVICE_BASE_ENDPOINT + AUTHORIZATION_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Authorization.class);
    
    @ApiOperation(value = ApiDocumentation.Authorization.GRANT_ATTRIBUTE)
    @RequestMapping(value = "/{federationId}/{memberId}/{attributeId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> grantAttribute(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Attributes.ATTRIBUTE_ID)
            @PathVariable String attributeId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
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
    
    @ApiOperation(value = ApiDocumentation.Authorization.REVOKE_ATTRIBUTE)
    @RequestMapping(value = "/{federationId}/{memberId}/{attributeId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> revokeAttribute(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Attributes.ATTRIBUTE_ID)
            @PathVariable String attributeId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
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
    
    @ApiOperation(value = ApiDocumentation.Authorization.IS_AUTHORIZED)
    @RequestMapping(value = "/Authorize/{federationId}/{serviceId}/{memberId}", method = RequestMethod.POST)
    public ResponseEntity<Authorized> isOperationAuthorized(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken, 
            @ApiParam(value = ApiDocumentation.Authorization.OPERATION_DESCRIPTION)
            @RequestBody OperationToAuthorize operationToAuthorize) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.IS_OPERATION_AUTHORIZED_RECEIVED);
            boolean isAuthorized = ApplicationFacade.getInstance().isAuthorized(systemUserToken, federationId, serviceId, 
                    memberId, operationToAuthorize);
            return new ResponseEntity<Authorized>(new Authorized(isAuthorized), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
