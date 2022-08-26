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
import cloud.fogbow.fhs.api.http.response.FedAdminInfo;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.Token;
import cloud.fogbow.fhs.api.parameters.FederationUpdate;
import cloud.fogbow.fhs.api.parameters.FederationUser;
import cloud.fogbow.fhs.api.parameters.FederationUserUpdate;
import cloud.fogbow.fhs.api.parameters.OperatorLoginData;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = FhsOperator.FHS_OPERATOR_ENDPOINT)
@Api(description = ApiDocumentation.FhsOperator.API)
public class FhsOperator {
    public static final String FHS_OPERATOR_SUFFIX_ENDPOINT = "FHSOperator";
    public static final String FHS_OPERATOR_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + FHS_OPERATOR_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(FhsOperator.class);
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.CREATE_FED_ADMIN_OPERATION)
    @RequestMapping(value = "/NewFedAdmin", method = RequestMethod.POST)
    public ResponseEntity<MemberId> addFedAdmin(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.FhsOperator.CREATE_FED_ADMIN_BODY)
            @RequestBody FederationUser fedAdmin) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.ADD_FEDERATION_ADMIN_RECEIVED);
            String memberId = ApplicationFacade.getInstance().addFederationAdmin(systemUserToken, fedAdmin.getName(), 
                    fedAdmin.getEmail(), fedAdmin.getDescription(), fedAdmin.getEnabled(), fedAdmin.getAuthenticationProperties());
            return new ResponseEntity<>(new MemberId(memberId), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.GET_FED_ADMINS_OPERATION)
    @RequestMapping(value = "/FedAdmins", method = RequestMethod.GET)
    public ResponseEntity<List<FedAdminInfo>> listFedAdmins(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.LIST_FED_ADMINS_RECEIVED);
            List<FedAdminInfo> fedAdmins = ApplicationFacade.getInstance().getFederationAdmins(systemUserToken);
            return new ResponseEntity<>(fedAdmins, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.UPDATE_FED_ADMIN_OPERATION)
    @RequestMapping(value = "/FedAdmin/{memberId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateFedAdmin(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId,
            @ApiParam(value = ApiDocumentation.FhsOperator.UPDATE_FED_ADMIN_BODY)
            @RequestBody FederationUserUpdate fedAdminUpdate) throws FogbowException {
        try { 
            LOGGER.info(Messages.Log.UPDATE_FED_ADMIN_RECEIVED);
            ApplicationFacade.getInstance().updateFederationAdmin(systemUserToken, memberId, fedAdminUpdate.getMemberName(), 
                    fedAdminUpdate.getEmail(), fedAdminUpdate.getDescription(), fedAdminUpdate.getEnabled());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.DELETE_FED_ADMIN_OPERATION)
    @RequestMapping(value = "/FedAdmin/{memberId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFedAdmin(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.CommonParameters.MEMBER_ID)
            @PathVariable String memberId) throws FogbowException {
        try { 
            LOGGER.info(Messages.Log.DELETE_FED_ADMIN_RECEIVED);
            ApplicationFacade.getInstance().deleteFederationAdmin(systemUserToken, memberId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.GET_FEDERATION_INSTANCES_OPERATION)
    @RequestMapping(value = "/FedInstances", method = RequestMethod.GET)
    public ResponseEntity<List<FederationInstance>> listFederationInstances(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.LIST_FEDERATION_INSTANCES_RECEIVED);
            List<FederationInstance> federationInstances = ApplicationFacade.getInstance().listFederationInstances(systemUserToken);
            return new ResponseEntity<>(federationInstances, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.UPDATE_FEDERATION_INSTANCE_OPERATION)
    @RequestMapping(value = "/FedInstance/{federationId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateFederation(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId,
            @ApiParam(value = ApiDocumentation.FhsOperator.UPDATE_FEDERATION_BODY)
            @RequestBody FederationUpdate federationUpdate) throws FogbowException {
        try { 
            LOGGER.info(Messages.Log.UPDATE_FEDERATION_RECEIVED);
            ApplicationFacade.getInstance().updateFederation(systemUserToken, federationId, federationUpdate.isEnabled());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.DELETE_FEDERATION_INSTANCE_OPERATION)
    @RequestMapping(value = "/FedInstance/{federationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFederation(
            @ApiParam(value = ApiDocumentation.Authentication.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @ApiParam(value = ApiDocumentation.Federation.FEDERATION_ID)
            @PathVariable String federationId) throws FogbowException {
        try { 
            LOGGER.info(Messages.Log.DELETE_FEDERATION_INSTANCE_RECEIVED);
            ApplicationFacade.getInstance().deleteFederationInstance(systemUserToken, federationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Log.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.LOGIN_OPERATION)
    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    public ResponseEntity<Token> operatorLogin(
            @ApiParam(value = ApiDocumentation.FhsOperator.LOGIN_REQUEST_BODY)
            @RequestBody OperatorLoginData loginData) throws FogbowException { 
        try {
            LOGGER.info(Messages.Log.OPERATOR_LOGIN_RECEIVED);
            String encryptedToken = ApplicationFacade.getInstance().operatorLogin(loginData.getOperatorId(), loginData.getCredentials());
            return new ResponseEntity<Token>(new Token(encryptedToken), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.FhsOperator.RELOAD_OPERATION)
    @RequestMapping(value = "/reload", method = RequestMethod.POST)
    public ResponseEntity<Boolean> reload(
            @ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        LOGGER.info(Messages.Log.RELOAD_CONFIGURATION_RECEIVED);
        ApplicationFacade.getInstance().reload(systemUserToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
