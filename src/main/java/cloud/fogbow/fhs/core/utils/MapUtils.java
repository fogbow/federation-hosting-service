package cloud.fogbow.fhs.core.utils;

import java.util.Map;

import com.google.gson.Gson;

public class MapUtils {
    
    public String serializeMap(Map<String, String> map) {
        return new Gson().toJson(map);
    }
    
    public Map<String, String> deserializeMap(String serializedMap) {
        return new Gson().fromJson(serializedMap, Map.class);
    }
}
