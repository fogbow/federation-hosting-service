package cloud.fogbow.fhs.core.models;

public enum OperationType {
    ADD_FED_ADMIN("addFedAdmin"), 
    CREATE_FEDERATION("createFederation"), 
    LIST_FEDERATIONS("listFederations"), 
    GRANT_MEMBERSHIP("grantMembership"), 
    LIST_MEMBERS("listFederationMembers"), 
    REGISTER_SERVICE("registerService"), 
    GET_SERVICES("getServices"), 
    GET_SERVICE("getService"), 
    DISCOVER_SERVICES("discoverServices"), 
    INVOKE("invoke"),
    MAP("map"),
    CREATE_ATTRIBUTE("createAttribute"),
    GET_ATTRIBUTES("getAttributes"),
    GRANT_ATTRIBUTE("grantAttribute"),
    REVOKE_ATTRIBUTE("revokeAttribute");

    private String value;
    
    private OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
