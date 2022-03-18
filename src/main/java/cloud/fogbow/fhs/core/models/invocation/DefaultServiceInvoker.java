package cloud.fogbow.fhs.core.models.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceInvoker;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.response.DefaultServiceResponse;

public class DefaultServiceInvoker implements ServiceInvoker {

    @Override
    public String getName() {
        // FIXME constant
        return "DefaultServiceInvoker";
    }

    @Override
    public ServiceResponse invoke(FederationUser user, String endpoint, HttpMethod method, List<String> path,
            Map<String, String> headers, Map<String, String> body) throws FogbowException {
        HttpResponse response = HttpRequestClient.doGenericRequest(method, endpoint, headers, body);
        Map<String, String> contentMap = new HashMap<String, String>();
        // FIXME constant
        contentMap.put("content", response.getContent());
        return new DefaultServiceResponse(response.getHttpCode(), contentMap);
    }
}
