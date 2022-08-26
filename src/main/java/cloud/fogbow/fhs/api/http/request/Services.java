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
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.api.parameters.Service;
import cloud.fogbow.fhs.api.parameters.ServiceUpdate;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Services.SERVICES_ENDPOINT)
@Api(description = ApiDocumentation.Services.API)
public class Services {
    public static final String SERVICES_SUFFIX_ENDPOINT = "Services";
    public static final String SERVICES_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + SERVICES_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Services.class);
    
    @ApiOperation(value = ApiDocumentation.Services.CREATE_OPERATION)
    @RequestMapping(value = "/{federationId}", method = RequestMethod.POST)
    public ResponseEntity<ServiceId> registerService(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Services.CREATE_REQUEST_BODY)
            @RequestBody Service service) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.REGISTER_SERVICE_RECEIVED);
            ServiceId serviceId = ApplicationFacade.getInstance().registerService(systemUserToken, federationId, 
                    service.getOwnerId(), service.getEndpoint(), service.getMetadata(), service.getDiscoveryPolicy(), 
                    service.getAccessPolicy());
            return new ResponseEntity<>(serviceId, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Services.GET_OPERATION)
    @RequestMapping(value = "/{federationId}/{ownerId}", method = RequestMethod.GET)
    public ResponseEntity<List<ServiceId>> getServices(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String ownerId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_SERVICES_RECEIVED);
            List<ServiceId> serviceId = ApplicationFacade.getInstance().getServices(systemUserToken, federationId, ownerId);
            return new ResponseEntity<>(serviceId, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }

    @ApiOperation(value = ApiDocumentation.Services.GET_BY_ID_OPERATION)
    @RequestMapping(value = "/{federationId}/{ownerId}/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceInfo> getService(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String ownerId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_SERVICE_RECEIVED);
            ServiceInfo serviceInfo = ApplicationFacade.getInstance().getService(systemUserToken, federationId, 
                    ownerId, serviceId);
            return new ResponseEntity<>(serviceInfo, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Services.DELETE_OPERATION)
    @RequestMapping(value = "/{federationId}/{ownerId}/{serviceId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteService(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String ownerId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.DELETE_SERVICE_RECEIVED);
            ApplicationFacade.getInstance().deleteService(systemUserToken, federationId, ownerId, serviceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Services.UPDATE_OPERATION)
    @RequestMapping(value = "/{federationId}/{ownerId}/{serviceId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateService(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String ownerId,
            @ApiParam(value = ApiDocumentation.Services.SERVICE_ID)
            @PathVariable String serviceId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Services.UPDATE_REQUEST_BODY)
            @RequestBody ServiceUpdate serviceUpdate) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.UPDATE_SERVICE_RECEIVED);
            ApplicationFacade.getInstance().updateService(systemUserToken, federationId, ownerId, serviceId, 
                    serviceUpdate.getMetadata(), serviceUpdate.getDiscoveryPolicy(), serviceUpdate.getAccessPolicy());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
