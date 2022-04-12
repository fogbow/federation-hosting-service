package cloud.fogbow.fhs.constants;

public class Messages {

    public static class Exception {
        public static final String ADMIN_ALREADY_EXISTS = "Admin already exists.";
        public static final String ADMIN_NAME_CANNOT_BE_NULL_OR_EMPTY = "Admin name cannot be null or empty.";
        public static final String CANNOT_FIND_FEDERATION = "Cannot find federation %s.";
        public static final String FEDERATION_NAME_CANNOT_BE_NULL_OR_EMPTY = "Federation name cannot be null or empty.";
        public static final String GENERIC_EXCEPTION_S = Log.GENERIC_EXCEPTION_S;
        public static final String MEMBER_NOT_FOUND_IN_FEDERATION = "Member %s not found in federation %s.";
        public static final String NO_OPERATOR_ID_SPECIFIED = "No operator ID specified in the configuration file.";
        public static final String NO_OTHER_SERVICE_ADMIN_ID_SPECIFIED = "No other service admin ID specified in the configuration file.";
        public static final String REQUESTER_IS_NOT_ADMIN = "Requester is not admin.";
        public static final String SERVICE_ENDPOINT_CANNOT_BE_NULL_OR_EMPTY = "Service endpoint cannot be null or empty.";
        public static final String SERVICE_OWNER_CANNOT_BE_NULL_OR_EMPTY = "Service owner cannot be null or empty.";
        public static final String UNABLE_TO_FIND_CLASS_S = "Unable to find class %s.";
    }
    
    public static class Log {
        public static final String ADD_FEDERATION_ADMIN_RECEIVED = "Add federation admin received.";
        public static final String CANNOT_FIND_SERVICE = "Cannot find service %s.";
        public static final String CREATE_FEDERATION_RECEIVED = "Create federation received.";
        public static final String DISCOVER_SERVICES_RECEIVED = "Discover services received.";
        public static final String GENERIC_EXCEPTION_S = "Operation returned error: %s.";
        public static final String GET_FEDERATIONS_RECEIVED = "Get federations received.";
        public static final String GET_PUBLIC_KEY = "Get public key received.";
        public static final String GET_SERVICE_RECEIVED = "Get service received.";
        public static final String GET_SERVICES_RECEIVED = "Get services received.";
        public static final String GRANT_MEMBERSHIP_RECEIVED = "Grant membership received.";
        public static final String INVOKE_DELETE_REQUEST_RECEIVED = "Invoke delete request received.";
        public static final String INVOKE_GET_REQUEST_RECEIVED = "Invoke get request received.";
        public static final String INVOKE_POST_REQUEST_RECEIVED = "Invoke post request received.";
        public static final String INVOKE_PUT_REQUEST_RECEIVED = "Invoke put request received.";
        public static final String LIST_MEMBERS_RECEIVED = "List members received.";
        public static final String MAP_RECEIVED = "Map received.";
        public static final String REGISTER_SERVICE_RECEIVED = "Register service received.";
    }
}
