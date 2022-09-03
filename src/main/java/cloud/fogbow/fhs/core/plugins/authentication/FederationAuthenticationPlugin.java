package cloud.fogbow.fhs.core.plugins.authentication;

import java.util.Map;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;

public interface FederationAuthenticationPlugin {
    String authenticate(Map<String, String> authenticationData) 
            throws UnauthenticatedUserException, ConfigurationErrorException, InternalServerErrorException;
    SystemUser validateToken(String token) throws UnauthenticatedUserException, InternalServerErrorException;
}
