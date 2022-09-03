package cloud.fogbow.fhs.api.http.request;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.Token;
import cloud.fogbow.fhs.api.parameters.FederationAdminLoginData;
import cloud.fogbow.fhs.api.parameters.LoginData;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Authentication.LOGIN_ENDPOINT)
@Api(description = ApiDocumentation.Authentication.API)
public class Authentication {
    public static final String LOGIN_SUFFIX_ENDPOINT = "MemberLogin";
    public static final String LOGIN_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + LOGIN_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Authentication.class);
    
    @ApiOperation(value = ApiDocumentation.Authentication.LOGIN_OPERATION)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Token> login(
            @ApiParam(value = ApiDocumentation.Authentication.LOGIN_REQUEST_BODY)
            @RequestBody LoginData loginData) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.LOGIN_RECEIVED);
            String encryptedToken = ApplicationFacade.getInstance().login(loginData.getFederationId(), 
                    loginData.getMemberId(), loginData.getCredentials());
            return new ResponseEntity<Token>(new Token(encryptedToken), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }

    @RequestMapping(value = "/FedAdmin", method = RequestMethod.POST)
    public ResponseEntity<Token> federationAdminLogin(
            @ApiParam(value = ApiDocumentation.Authentication.ADMIN_LOGIN_REQUEST_BODY)
            @RequestBody FederationAdminLoginData loginData) throws FogbowException { 
        try {
            LOGGER.info(Messages.Log.FEDERATION_ADMIN_LOGIN_RECEIVED);
            String encryptedToken = ApplicationFacade.getInstance().federationAdminLogin(
                    loginData.getFederationAdminId(), loginData.getCredentials());
            return new ResponseEntity<Token>(new Token(encryptedToken), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
