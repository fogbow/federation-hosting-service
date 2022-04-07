package cloud.fogbow.fhs.core.plugins.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.plugins.response.ServiceResponse;

public class DefaultServiceInvoker extends HttpServiceInvoker {
    public static final String RESPONSE_CONTENT_KEY = "content";
    private static final String INVOKER_NAME = "DefaultServiceInvoker";

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
        return body;
    }

    @Override
    ServiceResponse prepareResponse(HttpResponse response) {
        Map<String, String> contentMap = new HashMap<String, String>();
        contentMap.put(RESPONSE_CONTENT_KEY, response.getContent());
        return new DefaultServiceResponse(response.getHttpCode(), contentMap);
    }
}
