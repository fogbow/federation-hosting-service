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
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Services.SERVICES_ENDPOINT)
public class Services {
    public static final String SERVICES_SUFFIX_ENDPOINT = "Services";
    public static final String SERVICES_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + SERVICES_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Services.class);
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.POST)
    public ResponseEntity<ServiceId> registerService(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
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
    
    @RequestMapping(value = "/{federationId}/{ownerId}", method = RequestMethod.GET)
    public ResponseEntity<List<ServiceId>> getServices(
            @PathVariable String federationId,
            @PathVariable String ownerId,
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

    @RequestMapping(value = "/{federationId}/{ownerId}/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceInfo> getService(
            @PathVariable String federationId,
            @PathVariable String ownerId,
            @PathVariable String serviceId,
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
}
