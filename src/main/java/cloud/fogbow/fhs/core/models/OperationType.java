package cloud.fogbow.fhs.core.models;

public enum OperationType {
    ADD_FED_ADMIN("addFedAdmin"), 
    CREATE_FEDERATION("createFederation"), 
    LIST_FEDERATIONS("listFederations"), 
    GET_FEDERATION_INFO("getFederationInfo"),
    DELETE_FEDERATION("deleteFederation"),
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
    DELETE_ATTRIBUTE("deleteAttribute");

    private String value;
    
    private OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
