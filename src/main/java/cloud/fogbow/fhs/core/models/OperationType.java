package cloud.fogbow.fhs.core.models;

public enum OperationType {
    ADD_FED_ADMIN("addFedAdmin"), 
    GET_FED_ADMINS("getFedAdmins"),
    UPDATE_FED_ADMIN("updateFedAdmin"),
    DELETE_FED_ADMIN("deleteFedAdmin"),
    LIST_FEDERATION_INSTANCES("listFederationInstances"),
    UPDATE_FEDERATION("updateFederation"),
    DELETE_FEDERATION_INSTANCE("deleteFederationInstance"),
    RELOAD_CONFIGURATION("reloadConfiguration"),
    CREATE_FEDERATION("createFederation"), 
    LIST_FEDERATIONS("listFederations"), 
    GET_FEDERATION_INFO("getFederationInfo"),
    DELETE_FEDERATION("deleteFederation"),
    GET_REMOTE_FEDERATION_LIST("getRemoteFederationList"),
    JOIN_REMOTE_FEDERATION("joinRemoteFederation"), 
    GET_REMOTE_USERS_ALLOWED_ADMINS("getRemoteUsersAllowedAdmins"),
    ADD_REMOTE_USER_TO_ALLOWED_ADMINS("addRemoteUserToAllowedAdmins"),
    REMOVE_REMOTE_USER_FROM_ALLOWED_ADMINS("removeRemoteUserFromAllowedAdmins"),
    GRANT_MEMBERSHIP("grantMembership"), 
    LIST_MEMBERS("listFederationMembers"), 
    GET_MEMBER_INFO("getMemberInfo"),
    REVOKE_MEMBERSHIP("revokeMembership"),
    REGISTER_SERVICE("registerService"), 
    GET_SERVICES("getServices"), 
    GET_SERVICE("getService"), 
    DELETE_SERVICE("deleteService"),
    UPDATE_SERVICE("updateService"), 
    DISCOVER_SERVICES("discoverServices"), 
    INVOKE("invoke"),
    MAP("map"),
    CREATE_ATTRIBUTE("createAttribute"),
    GET_ATTRIBUTES("getAttributes"),
    GRANT_ATTRIBUTE("grantAttribute"),
    REVOKE_ATTRIBUTE("revokeAttribute"), 
    DELETE_ATTRIBUTE("deleteAttribute"),
    SERVICE_ACCESS_AUTHORIZATION("serviceAccessAuthorization");

    private String value;
    
    private OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
