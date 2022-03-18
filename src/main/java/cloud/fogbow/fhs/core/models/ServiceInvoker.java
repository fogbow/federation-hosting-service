package cloud.fogbow.fhs.core.models;

import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;

public interface ServiceInvoker {
    String getName();
    ServiceResponse invoke(FederationUser user, String endpoint, HttpMethod method, List<String> path,
            Map<String, String> headers, Map<String, String> body) throws FogbowException;
}
