package cloud.fogbow.fhs.api.http.request;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.CommonKeys;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.parameters.FederationOwner;
import cloud.fogbow.fhs.api.parameters.FederationSpec;
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
        // TODO constant
        LOGGER.info("Receiving create federation request");
        FederationId federationId = ApplicationFacade.getInstance().createFederation(systemUserToken, federationSpec.getName(), 
                federationSpec.getMetadata(), federationSpec.getDescription(), federationSpec.getEnabled());
        return new ResponseEntity<>(federationId, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FederationDescription>> getFederations(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationOwner federationOwner) throws FogbowException {
        // TODO constant
        LOGGER.info("Receiving get federations request");
        List<FederationDescription> federationDescription = ApplicationFacade.getInstance().listFederations(systemUserToken, federationOwner.getOwner());
        return new ResponseEntity<>(federationDescription, HttpStatus.OK);
    }
}
