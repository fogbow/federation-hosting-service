package cloud.fogbow.fhs.constants;

public class Messages {

    public static class Exception {
        public static final String ADMIN_ALREADY_EXISTS = "Admin already exists.";
        public static final String ADMIN_NAME_CANNOT_BE_NULL_OR_EMPTY = "Admin name cannot be null or empty.";
        public static final String ATTRIBUTE_DOES_NOT_EXIST_IN_FEDERATION = "Attribute %s does not exist in federation %s.";
        public static final String CANNOT_FIND_FEDERATION = "Cannot find federation %s.";
        public static final String CANNOT_FIND_FEDERATION_ADMIN = "Cannot find federation admin %s.";
        public static final String EXPIRED_TOKEN = "Expired token.";
        public static final String FEDERATION_NAME_CANNOT_BE_NULL_OR_EMPTY = "Federation name cannot be null or empty.";
        public static final String GENERIC_EXCEPTION_S = Log.GENERIC_EXCEPTION_S;
        public static final String INVALID_FHS_OPERATOR_ID = "Invalid FHS Operator ID: %s.";
        public static final String INVALID_LOCAL_PROVIDER_ID = "Invalid local provider ID.";
        public static final String INVALID_SERVICE_PUBLIC_KEY_ENDPOINT = "Invalid service public key endpoint.";
        public static final String INVALID_TOKEN = "Invalid token.";
        public static final String MEMBER_NOT_FOUND_IN_FEDERATION = "Member %s not found in federation %s.";
        public static final String NO_OPERATOR_ID_SPECIFIED = "No operator ID specified in the configuration file.";
        public static final String NO_OTHER_SERVICE_ADMIN_ID_SPECIFIED = "No other service admin ID specified in the configuration file.";
        public static final String REQUESTER_DOES_NOT_OWN_FEDERATION = "Requester %s does not own federation %s.";
        public static final String REQUESTER_IS_NOT_ADMIN = "Requester is not admin.";
        public static final String REQUESTER_IS_NOT_SERVICE_OWNER = "Requester is not service owner.";
        public static final String SERVICE_ENDPOINT_CANNOT_BE_NULL_OR_EMPTY = "Service endpoint cannot be null or empty.";
        public static final String SERVICE_NOT_FOUND = "Service %s not found in federation %s.";
        public static final String SERVICE_OWNER_CANNOT_BE_NULL_OR_EMPTY = "Service owner cannot be null or empty.";
        public static final String UNABLE_TO_FIND_CLASS_S = "Unable to find class %s.";
        public static final String UNABLE_TO_RETRIEVE_RESPONSE_FROM_PROVIDER_S = "Unable to retrieve response from provider: %s.";
        public static final String USER_ALREADY_HAS_ATTRIBUTE = "User %s already has attribute %s.";
        public static final String USER_DOES_NOT_HAVE_ATTRIBUTE = "User %s does not have attribute %s.";
        public static final String USER_IS_NOT_AUTHORIZED_TO_PERFORM_OPERATION_ON_SERVICE = "User is not authorized to perform operation on service.";
    }
    
    public static class Log {
        public static final String ADD_FEDERATION_ADMIN_RECEIVED = "Add federation admin received.";
        public static final String CANNOT_FIND_SERVICE = "Cannot find service %s.";
        public static final String CANNOT_INITIALIZE_PACKAGE_SENDER = "Cannot initialize package sender.";
        public static final String CONNECTING_UP_PACKET_SENDER = "Connecting XMPP packet sender.";
        public static final String CREATE_FEDERATION_ATTRIBUTE_RECEIVED = "Create federation attribute received.";
        public static final String CREATE_FEDERATION_RECEIVED = "Create federation received.";
        public static final String DELETE_FEDERATION_ATTRIBUTE_RECEIVED = "Delete federation attribute received.";
        public static final String DELETE_FEDERATION_INSTANCE_RECEIVED = "Delete federation instance received.";
        public static final String DELETE_FEDERATION_RECEIVED = "Delte federation received.";
        public static final String DELETE_FED_ADMIN_RECEIVED = "Delete fed admin received.";
        public static final String DELETE_SERVICE_RECEIVED = "Delete service received.";
        public static final String DISCOVER_SERVICES_RECEIVED = "Discover services received.";
        public static final String FEDERATION_ADMIN_LOGIN_RECEIVED = "Federation admin login received.";
        public static final String GENERIC_EXCEPTION_S = "Operation returned error: %s.";
        public static final String GET_FEDERATION_ATTRIBUTES_RECEIVED = "Get federation attributes received.";
        public static final String GET_FEDERATION_INFO_RECEIVED = "Get federation info received.";
        public static final String GET_FEDERATIONS_RECEIVED = "Get federations received.";
        public static final String GET_JOIN_REQUESTS_RECEIVED = "Get join requests received.";
        public static final String GET_MEMBER_INFO_RECEIVED = "Get member info received.";
        public static final String GET_PUBLIC_KEY = "Get public key received.";
        public static final String GET_REMOTE_FEDERATION_LIST = "Get remote federation list.";
        public static final String GET_SERVICE_RECEIVED = "Get service received.";
        public static final String GET_SERVICES_RECEIVED = "Get services received.";
        public static final String GRANT_FEDERATION_ATTRIBUTE_RECEIVED = "Grant federation attribute received.";
        public static final String GRANT_MEMBERSHIP_RECEIVED = "Grant membership received.";
        public static final String INVOKE_DELETE_REQUEST_RECEIVED = "Invoke delete request received.";
        public static final String INVOKE_GET_REQUEST_RECEIVED = "Invoke get request received.";
        public static final String INVOKE_HEAD_REQUEST_RECEIVED = "Invoke head request received.";
        public static final String INVOKE_OPTIONS_REQUEST_RECEIVED = "Invoke options request received.";
        public static final String INVOKE_PATCH_REQUEST_RECEIVED = "Invoke patch request received.";
        public static final String INVOKE_POST_REQUEST_RECEIVED = "Invoke post request received.";
        public static final String INVOKE_PUT_REQUEST_RECEIVED = "Invoke put request received.";
        public static final String JOIN_DENY_RECEIVED = "Join deny received.";
        public static final String JOIN_GRANT_RECEIVED = "Join grant received.";
        public static final String JOIN_REMOTE_FEDERATION_RECEIVED = "Join remote federation received.";
        public static final String LIST_FEDERATION_INSTANCES_RECEIVED = "List federation instances received.";
        public static final String LIST_FED_ADMINS_RECEIVED = "List fed admins received.";
        public static final String LIST_MEMBERS_RECEIVED = "List members received.";
        public static final String LOGIN_RECEIVED = "Login received.";
        public static final String LOGOUT_RECEIVED = "Logout received.";
        public static final String MAP_RECEIVED = "Map received.";
        public static final String NO_PACKET_SENDER = "PacketSender was not initialized. Trying again.";
        public static final String NO_REMOTE_COMMUNICATION_CONFIGURED = "No remote communication configured.";
        public static final String OPERATOR_LOGIN_RECEIVED = "Operator login received.";
        public static final String PACKET_SENDER_INITIALIZED = "XMPP packet sender initialized.";
        public static final String RECEIVING_REMOTE_REQUEST_S = "Received remote request for request: %s.";
        public static final String REGISTER_SERVICE_RECEIVED = "Register service received.";
        public static final String RELOAD_CONFIGURATION_RECEIVED = "Reload configuration received.";
        public static final String REVOKE_FEDERATION_ATTRIBUTE_RECEIVED = "Revoke federation attribute received.";
        public static final String REVOKE_MEMBERSHIP_RECEIVED = "Revoke membership received.";
        public static final String SENDING_MSG_S = "Sending remote request for request: %s.";
        public static final String SUCCESS = "Successfully executed operation.";
        public static final String UNEXPECTED_ERROR_WITH_MESSAGE_S = "Unexpected exception error: %s.";
        public static final String UPDATE_FEDERATION_RECEIVED = "Update federation received.";
        public static final String UPDATE_FED_ADMIN_RECEIVED = "Update fed admin received.";
        public static final String UPDATE_SERVICE_RECEIVED = "Update service received.";
        public static final String XMPP_HANDLERS_SET = "XMPP handlers set.";
    }
}
