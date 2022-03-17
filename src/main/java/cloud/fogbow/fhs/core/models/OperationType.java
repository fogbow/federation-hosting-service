package cloud.fogbow.fhs.core.models;

public enum OperationType {
    ADD_FED_ADMIN("addFedAdmin"), 
    CREATE_FEDERATION("createFederation"), 
    LIST_FEDERATIONS("listFederations"), 
    GRANT_MEMBERSHIP("grantMembership"), 
    LIST_MEMBERS("listFederationMembers");

    private String value;
    
    private OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
