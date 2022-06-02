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
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.ApiParam;

// TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = FhsOperator.FHS_OPERATOR_ENDPOINT)
public class FhsOperator {
    public static final String FHS_OPERATOR_SUFFIX_ENDPOINT = "FHSOperator";
    public static final String FHS_OPERATOR_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + FHS_OPERATOR_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(FhsOperator.class);
    
    @RequestMapping(value = "/NewFedAdmin", method = RequestMethod.POST)
    public ResponseEntity<MemberId> addFedAdmin(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
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
    
    @RequestMapping(value = "/FedAdmins", method = RequestMethod.GET)
    public ResponseEntity<List<FedAdminInfo>> listFedAdmins(
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
    
    @RequestMapping(value = "/FedAdmin/{memberId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateFedAdmin(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @PathVariable String memberId,
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
    
    @RequestMapping(value = "/FedAdmin/{memberId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFedAdmin(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
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
    
    @RequestMapping(value = "/FedInstances", method = RequestMethod.GET)
    public ResponseEntity<List<FederationInstance>> listFederationInstances(
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
    
    @RequestMapping(value = "/FedInstance/{federationId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateFederation(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
            @PathVariable String federationId,
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
    
    @RequestMapping(value = "/FedInstance/{federationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFederation(
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
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
    
    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    public ResponseEntity<Token> operatorLogin(
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
    
    @RequestMapping(value = "/reload", method = RequestMethod.POST)
    public ResponseEntity<Boolean> reload(
            @ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
            @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        LOGGER.info(Messages.Log.RELOAD_CONFIGURATION_RECEIVED);
        ApplicationFacade.getInstance().reload(systemUserToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
