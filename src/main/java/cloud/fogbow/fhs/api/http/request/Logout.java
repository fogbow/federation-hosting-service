package cloud.fogbow.fhs.api.http.request;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.constants.ApiDocumentation;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;
import io.swagger.annotations.Api;

@CrossOrigin
@RestController
@RequestMapping(value = Logout.LOGOUT_ENDPOINT)
@Api(description = ApiDocumentation.Logout.API)
public class Logout {
    public static final String LOGOUT_SUFFIX_ENDPOINT = "MemberLogout";
    public static final String LOGOUT_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + LOGOUT_SUFFIX_ENDPOINT;
    
    private final Logger LOGGER = Logger.getLogger(Logout.class);
    
    @RequestMapping(value = "/{loginSessionId}", method = RequestMethod.DELETE)
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
