package cloud.fogbow.fhs.core.intercomponent.xmpp;

public enum RemoteMethod {
    REMOTE_GET_ALL_FEDERATIONS("remoteGetAllFederations"), 
    REMOTE_JOIN_FEDERATION("remoteJoinFederation"),
    SYNC_FEDERATIONS("syncFederations"),
    UPDATE_FEDERATION("updateFederation");
    
    private final String method;

    RemoteMethod(final String methodName) {
        this.method = methodName;
    }

    @Override
    public String toString() {
        return method;
    }
}
