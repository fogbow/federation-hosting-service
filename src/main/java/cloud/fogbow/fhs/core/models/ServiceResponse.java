package cloud.fogbow.fhs.core.models;

import java.util.Map;

public interface ServiceResponse {
    int getCode();
    Map<String, String> getResponse();
}
