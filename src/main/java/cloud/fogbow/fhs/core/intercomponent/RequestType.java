package cloud.fogbow.fhs.core.intercomponent;

public enum RequestType {
    NOTIFY_UPDATE("notifyUpdate"),
    GET_ALL_FEDERATIONS("getAllFederations");

    private String typeName;
    
    RequestType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
