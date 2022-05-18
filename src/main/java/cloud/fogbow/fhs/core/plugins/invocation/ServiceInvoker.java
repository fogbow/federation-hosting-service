package cloud.fogbow.fhs.core.plugins.invocation;

import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

public interface ServiceInvoker {
    String getName();
    ServiceResponse invoke(FederationUser user, String serviceId, String endpoint, HttpMethod method,
            List<String> path, Map<String, String> headers, Map<String, Object> body) throws FogbowException;
}
