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
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationInfo;
import cloud.fogbow.fhs.api.parameters.FederationOwner;
import cloud.fogbow.fhs.api.parameters.FederationSpec;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Federation.FEDERATION_ENDPOINT)
public class Federation {
    public static final String FEDERATION_SUFFIX_ENDPOINT = "Federation";
    public static final String FEDERATION_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + FEDERATION_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Federation.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FederationId> createFederation(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationSpec federationSpec) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.CREATE_FEDERATION_RECEIVED);
            FederationId federationId = ApplicationFacade.getInstance().createFederation(systemUserToken, federationSpec.getName(), 
                    federationSpec.getMetadata(), federationSpec.getDescription(), federationSpec.getEnabled());
            return new ResponseEntity<>(federationId, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FederationDescription>> getFederations(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationOwner federationOwner) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_FEDERATIONS_RECEIVED);
            List<FederationDescription> federationDescription = ApplicationFacade.getInstance().listFederations(systemUserToken, federationOwner.getOwner());
            return new ResponseEntity<>(federationDescription, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.GET)
    public ResponseEntity<FederationInfo> getFederationInfo(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationOwner federationOwner) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_FEDERATION_INFO_RECEIVED);
            FederationInfo federationInfo = ApplicationFacade.getInstance().getFederationInfo(systemUserToken, federationId);
            return new ResponseEntity<>(federationInfo, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFederation(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationOwner federationOwner) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.DELETE_FEDERATION_RECEIVED);
            ApplicationFacade.getInstance().deleteFederation(systemUserToken, federationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
