package cloud.fogbow.fhs.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
    private Gson gson;
    
    public JsonUtils() {
        gson = new Gson();
    }
    
    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    public <T> T fromJson(String json, TypeToken<T> t) {
        return gson.fromJson(json, t.getType());
    }
    
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
