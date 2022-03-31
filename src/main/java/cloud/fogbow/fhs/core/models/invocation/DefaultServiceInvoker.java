package cloud.fogbow.fhs.core.models.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.core.models.ServiceResponse;
import cloud.fogbow.fhs.core.models.response.DefaultServiceResponse;

public class DefaultServiceInvoker extends HttpServiceInvoker {
    private static final String INVOKER_NAME = "DefaultServiceInvoker";
    private static final String RESPONSE_CONTENT_KEY = "content";

    @Override
    public String getName() {
        return INVOKER_NAME;
    }

    @Override
    List<String> preparePath(List<String> path) {
        return path;
    }

    @Override
    Map<String, String> prepareHeaders(Map<String, String> headers) {
        return headers;
    }

    @Override
    Map<String, Object> prepareBody(Map<String, Object> body) {
        return new HashMap<String, Object>(body);
    }

    @Override
    ServiceResponse prepareResponse(HttpResponse response) {
        Map<String, String> contentMap = new HashMap<String, String>();
        contentMap.put(RESPONSE_CONTENT_KEY, response.getContent());
        return new DefaultServiceResponse(response.getHttpCode(), contentMap);
    }
}
