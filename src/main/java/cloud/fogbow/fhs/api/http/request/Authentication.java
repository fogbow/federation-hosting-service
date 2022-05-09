package cloud.fogbow.fhs.api.http.request;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.Token;
import cloud.fogbow.fhs.api.parameters.LoginData;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

//TODO documentation
@CrossOrigin
@RestController
@RequestMapping(value = Authentication.LOGIN_ENDPOINT)
public class Authentication {
    public static final String LOGIN_SUFFIX_ENDPOINT = "MemberLogin";
    public static final String LOGIN_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + LOGIN_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Authentication.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Token> login(
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
    
    // FIXME
    @RequestMapping(value = "/MemberLogout/{loginSessionId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> logout(
            @PathVariable String loginSessionId) throws FogbowException {
        try {
            LOGGER.info(Messages.Log.LOGOUT_RECEIVED);
            ApplicationFacade.getInstance().logout(loginSessionId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e.getMessage()), e);
            throw e;
        }
    }
}
