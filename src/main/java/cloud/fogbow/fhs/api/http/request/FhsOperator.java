package cloud.fogbow.fhs.api.http.request;

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
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.parameters.FederationUser;
import cloud.fogbow.fhs.constants.Messages;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.ApplicationFacade;

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
}
