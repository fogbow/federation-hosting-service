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
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Membership.MEMBERSHIP_ENDPOINT)
@Api(description = ApiDocumentation.Membership.API)
public class Membership {
    public static final String MEMBERSHIP_SUFFIX_ENDPOINT = "Membership";
    public static final String MEMBERSHIP_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + MEMBERSHIP_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Membership.class);
    
    @ApiOperation(value = ApiDocumentation.Membership.GRANT_MEMBERSHIP_OPERATION)
    @RequestMapping(value = "/{federationId}", method = RequestMethod.POST)
    public ResponseEntity<MemberId> grantMembership(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Membership.GRANT_MEMBERSHIP_BODY)
            @RequestBody FederationUser federationUser) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GRANT_MEMBERSHIP_RECEIVED);
            MemberId memberId = ApplicationFacade.getInstance().grantMembership(systemUserToken, federationId, federationUser.getName(), 
                    federationUser.getEmail(), federationUser.getDescription(), federationUser.getAuthenticationProperties());
            return new ResponseEntity<>(memberId, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Membership.GET_OPERATION)
    @RequestMapping(value = "/{federationId}", method = RequestMethod.GET)
    public ResponseEntity<List<FederationMember>> listMembers(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.LIST_MEMBERS_RECEIVED);
            List<FederationMember> members = ApplicationFacade.getInstance().listMembers(systemUserToken, federationId);
            return new ResponseEntity<>(members, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Membership.GET_BY_ID_OPERATION)
    @RequestMapping(value = "/{federationId}/{memberId}", method = RequestMethod.GET)
    public ResponseEntity<FederationMember> getMemberInfo(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.GET_MEMBER_INFO_RECEIVED);
            FederationMember member = ApplicationFacade.getInstance().getMemberInfo(systemUserToken, federationId, memberId);
            return new ResponseEntity<>(member, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Membership.REVOKE_MEMBERSHIP_OPERATION)
    @RequestMapping(value = "/{federationId}/{memberId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> revokeMembership(
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.REVOKE_MEMBERSHIP_RECEIVED);
            ApplicationFacade.getInstance().revokeMembership(systemUserToken, federationId, memberId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
