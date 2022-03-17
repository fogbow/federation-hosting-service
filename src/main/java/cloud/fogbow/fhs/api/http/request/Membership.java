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
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.parameters.FederationUser;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Membership.MEMBERSHIP_ENDPOINT)
public class Membership {
    public static final String MEMBERSHIP_SUFFIX_ENDPOINT = "Membership";
    public static final String MEMBERSHIP_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + MEMBERSHIP_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Membership.class);
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.POST)
    public ResponseEntity<MemberId> grantMembership(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @RequestBody FederationUser federationUser) throws FogbowException {
        // TODO constant
        LOGGER.info("Receiving grant membership request");
        MemberId memberId = ApplicationFacade.getInstance().grantMembership(systemUserToken, federationId, federationUser.getName());
        return new ResponseEntity<>(memberId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{federationId}", method = RequestMethod.GET)
    public ResponseEntity<List<FederationMember>> listMembers(
            @PathVariable String federationId,
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        // TODO constant
        LOGGER.info("Receiving list members request");
        List<FederationMember> members = ApplicationFacade.getInstance().listMembers(systemUserToken, federationId);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}
