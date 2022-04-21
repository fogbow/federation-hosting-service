package cloud.fogbow.fhs.core.plugins.access;

import java.util.Map;

import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceOperation;

public interface ServiceAccessPolicy {
    Map<String, String> getCredentialsForAccess(FederationUser user, String cloudName); 
    boolean isAllowedToPerform(FederationUser user, ServiceOperation operation);
}
