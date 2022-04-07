package cloud.fogbow.fhs.core.plugins.invocation;

import java.util.List;
import java.util.Map;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

public abstract class HttpServiceInvoker implements ServiceInvoker {
    abstract List<String> preparePath(List<String> path);
    abstract Map<String, String> prepareHeaders(Map<String, String> headers) throws FogbowException;
    abstract Map<String, Object> prepareBody(Map<String, Object> body)  throws FogbowException;
    abstract ServiceResponse prepareResponse(HttpResponse response)  throws FogbowException;
    
    @Override
    public ServiceResponse invoke(FederationUser user, String endpoint, HttpMethod method, List<String> path,
            Map<String, String> headers, Map<String, Object> body) throws FogbowException {
        List<String> preparedPath = preparePath(path);
        Map<String, String> preparedHeaders = prepareHeaders(headers);
        Map<String, Object> preparedBody = prepareBody(body);
        
        String completeEndpoint = endpoint + "/" + String.join("/", preparedPath);
        
        HttpResponse response = HttpRequestClient.doGenericRequestGenericBody(method, completeEndpoint, 
                preparedHeaders, preparedBody);
        return prepareResponse(response);
    }
}
