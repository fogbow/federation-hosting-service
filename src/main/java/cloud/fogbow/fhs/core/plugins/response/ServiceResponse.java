package cloud.fogbow.fhs.core.plugins.response;

import java.util.Map;

public interface ServiceResponse {
    int getCode();
    Map<String, String> getResponse();
}
