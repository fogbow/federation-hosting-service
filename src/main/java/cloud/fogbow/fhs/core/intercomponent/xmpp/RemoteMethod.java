package cloud.fogbow.fhs.core.intercomponent.xmpp;

public enum RemoteMethod {
    REMOTE_GET_ALL_FEDERATIONS("remoteGetAllFederations"), 
    SYNC_FEDERATIONS("syncFederations");
    
    private final String method;

    RemoteMethod(final String methodName) {
        this.method = methodName;
    }

    @Override
    public String toString() {
        return method;
    }
}
