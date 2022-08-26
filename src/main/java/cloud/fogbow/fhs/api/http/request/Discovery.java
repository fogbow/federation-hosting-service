package cloud.fogbow.fhs.api.http.request;

import java.util.List;

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
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Discovery.DISCOVERY_ENDPOINT)
@Api(description = ApiDocumentation.Discovery.API)
public class Discovery {
    public static final String DISCOVERY_SUFFIX_ENDPOINT = "Discovery";
    public static final String DISCOVERY_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + DISCOVERY_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Discovery.class);
    
    @ApiOperation(value = ApiDocumentation.Discovery.DISCOVER_SERVICES)
    @RequestMapping(value = "/{federationId}/{memberId}", method = RequestMethod.GET)
    public ResponseEntity<List<ServiceDiscovered>> discoverServices(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.DISCOVER_SERVICES_RECEIVED);
            List<ServiceDiscovered> serviceId = ApplicationFacade.getInstance().discoverServices(systemUserToken, federationId, memberId);
            return new ResponseEntity<>(serviceId, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
