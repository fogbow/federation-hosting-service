package cloud.fogbow.fhs.constants;

public class ApiDocumentation {
    public static class ApiInfo {
        public static final String API_TITLE = "Fogbow Federation Hosting Service API";
        public static final String API_DESCRIPTION =
                "This documentation introduces readers to Fogbow FHS REST API, provides guidelines on\n" +
                        "how to use it, and describes the available features accessible from it.";
    }
    
    public static class Authentication {
        public static final String API = "Manages user authentication operations.";
        public static final String LOGIN_OPERATION = "Creates tokens that federation members can later " + 
                                                     "use to authenticate themselves.";
        public static final String LOGIN_REQUEST_BODY =
                "The body of the request must specify the federation member credentials that are needed to authenticate. " +
                "It also must specify the ID of the member and the ID of the member's federation. " +
                "Credentials are specified as a map, and the values and keys required vary, depending " +
                "on the authentication method used by the federation member. " +
                "It is also necessary to provide the public key of the target service with " +
                "which the member will interact. Tokens are encrypted using this key, so that only the target " +
                "service is able to decrypt the token.";
        public static final String ADMIN_LOGIN_REQUEST_BODY = 
                "The body of the request must specify the federation admin credentials that are needed to authenticate. " +
                "It also must specify the ID of the federation admin. " +
                "Credentials are specified as a map, and the values and keys required vary, depending " +
                "on the authentication method used by the federation admin. " +
                "It is also necessary to provide the public key of the target service with " +
                "which the admin will interact. Tokens are encrypted using this key, so that only the target " +
                "service is able to decrypt the token.";
        public static final String SYSTEM_USER_TOKEN = 
                "This is the token that identifies a user in the FHS.";
    }
    
    public static class Attributes {
        public static final String API = "Manages operations on federation attributes.";
        public static final String CREATE_OPERATION = "Creates a federation attribute.";
        public static final String ATTRIBUTE_SPECIFICATION = "The properties of the attribute to be created.";
        public static final String GET_OPERATION = "Lists all attributes owned by the given federation.";
        public static final String DELETE_OPERATION = "Deletes a specific attribute instance.";
        public static final String ATTRIBUTE_ID = "The ID of the specific attribute instance.";
    }
    
    public static class Authorization { 
        public static final String API = "Manages members attributes and operation authorization.";
        public static final String GRANT_ATTRIBUTE = "Grants an attribute to a given member.";
        public static final String REVOKE_ATTRIBUTE = "Revokes an attribute from a given member.";
        public static final String IS_AUTHORIZED = "Verifies if the given operation can be performed by the given member.";
        public static final String OPERATION_DESCRIPTION = "The operation to be authorized.";
    }
    
    public static class Discovery {
        public static final String API = "Manages service discovery by federation members.";
        public static final String DISCOVER_SERVICES = "Lists all services available to the given member at the given federation.";
    }
    
    public static class Federation {
        public static final String API = "Manages federations.";
        public static final String CREATE_OPERATION = "Creates a federation instance.";
        public static final String CREATE_REQUEST_BODY = 
                "The body of the request must specify the name of the federation, " +
                "a description, its metadata and if the federation is enabled or not.";
        public static final String GET_OPERATION = "Lists all federations owned by the given user.";
        public static final String FEDERATION_OWNER = "The ID of the specific federation owner.";
        public static final String GET_BY_ID_OPERATION = "Lists a specific federation instance.";
        public static final String DELETE_OPERATION = "Deletes a specific federation instance.";
        public static final String GET_REMOTE_FEDERATIONS_OPERATION = "Lists the kwown remote federation instances.";
        public static final String JOIN_REMOTE_FEDERATION_OPERATION = 
                "Requests a remote FHS to add the given federation admin to the list of admins of a remote federation. " +
                "If the operation is successful, the given remote federation is added to the local list of federations.";
        public static final String GET_JOIN_REQUESTS_OPERATION = 
                "Lists the remote federation admins allowed to join local federations.";
        public static final String GRANT_JOIN_REQUEST_OPERATION = 
                "Adds the given remote federation admin to the list of admins allowed to join a specific local federation.";
        public static final String REQUEST_ID = "Parameter used to comply with IEEE P2302.";
        public static final String REMOTE_MEMBERSHIP = 
                "A join authorization rule, comprising a federation admin ID, the ID of the FHS where the admin was created " +
                "and the ID of the local federation where the admin is allowed to join.";
        public static final String DENY_JOIN_REQUEST_OPERATION = 
                "Removes the given remote federation admin from the list of admins allowed to join a specific local federation.";
        public static final String FEDERATION_ID = "The ID of the specific target federation.";
    }

    public static class FhsOperator {
        public static final String API = "Manages FHS-Operator-only operations.";
        public static final String CREATE_FED_ADMIN_OPERATION = "Creates a new federation admin.";
        public static final String CREATE_FED_ADMIN_BODY = 
                "The body of the request must specify the name of the federation admin, " + 
                "if the federation admin is enabled and its authentication parameters. " + 
                "The authentication parameters are used to create a FederationAuthenticationPlugin instance, " + 
                "which performs the admin authentication. The authentication parameters are specified as a map, " +
                "and the values and keys required vary, depending on the authentication method used by the federation admin. " +
                "But normally the map should contain the endpoint of an external authentication service.";
        public static final String GET_FED_ADMINS_OPERATION = "Lists all federation admins.";
        public static final String UPDATE_FED_ADMIN_OPERATION = "Updates a specific federation admin.";
        public static final String UPDATE_FED_ADMIN_BODY = 
                "The body of the request must specify the new name for the federation admin and " +
                "if the admin is enabled or not.";
        public static final String DELETE_FED_ADMIN_OPERATION = "Deletes a specific federation admin.";
        public static final String GET_FEDERATION_INSTANCES_OPERATION = "Lists all federation instances.";
        public static final String UPDATE_FEDERATION_INSTANCE_OPERATION = "Updates a federation instance.";
        public static final String UPDATE_FEDERATION_BODY = 
                "The body of the request must specify if the federation is enabled or not.";
        public static final String DELETE_FEDERATION_INSTANCE_OPERATION = "Deletes a specific federation instance.";
        public static final String LOGIN_OPERATION = "Creates tokens that FHS-Operators can later " + 
                "use to authenticate themselves.";
        public static final String LOGIN_REQUEST_BODY = 
                "The body of the request must specify the FHS-Operator credentials that are needed to authenticate. " +
                "It also must specify the ID of the FHS-Operator. " +
                "Credentials are specified as a map, and the values and keys required vary, depending " +
                "on the authentication method used by the FHS-Operator. " +
                "It is also necessary to provide the public key of the target service with " +
                "which the FHS-Operator will interact. Tokens are encrypted using this key, so that only the target " +
                "service is able to decrypt the token.";
        public static final String RELOAD_OPERATION = "Reloads configuration parameters.";
    }
    
    public static class Invocation {
        public static final String API = "Manages federation services invocation.";
        public static final String INVOKE_GET = "Invokes the given service using the \"get\" method.";
        public static final String INVOKE_POST = "Invokes the given service using the \"post\" method.";
        public static final String INVOKE_PUT = "Invokes the given service using the \"put\" method.";
        public static final String INVOKE_DELETE = "Invokes the given service using the \"delete\" method.";
        public static final String INVOKE_PATCH = "Invokes the given service using the \"patch\" method.";
        public static final String INVOKE_OPTIONS = "Invokes the given service using the \"options\" method.";
        public static final String INVOKE_HEAD = "Invokes the given service using the \"head\" method.";
        public static final String INVOCATION_BODY = 
                "The body of the request must specify a complement of the service endpoint, if necessary, " + 
                "the service invocation headers and the service invocation body. " +
                "The complement is specified as a list of Strings, which is concatenated to the " +
                "service endpoint to produce the URL used in the invocation. " + 
                "The headers and the body are specified as maps.";
    }
    
    public static class Logout {
        public static final String API = "Manages logout.";
    }
    
    public static class Map {
        public static final String API = "Manages credentials mapping.";
        public static final String MAP_OPERATION = 
                "Returns the required credentials for the given user to access the given cloud through the given service.";
        public static final String CLOUD_NAME = "The name of the cloud to be accessed.";
    }
    
    public static class Membership {
        public static final String API = "Manages federation membership.";
        public static final String GRANT_MEMBERSHIP_OPERATION = "Registers given user as a member of the given federation.";
        public static final String GET_OPERATION = "Lists all members of a federation.";
        public static final String GRANT_MEMBERSHIP_BODY = 
                "The body of the request must specify the name of the member, if the member is enabled and its authentication parameters. " + 
                "The authentication parameters are used to create a FederationAuthenticationPlugin instance, " + 
                "which performs the member authentication. The authentication parameters are specified as a map, " +
                "and the values and keys required vary, depending on the authentication method used by the member. " +
                "But normally the map should contain the endpoint of an external authentication service.";
        public static final String GET_BY_ID_OPERATION = "Lists a specific member.";
        public static final String REVOKE_MEMBERSHIP_OPERATION = "Revokes federation membership for a given user.";
    }
    
    public static class Services {
        public static final String API = "Manages federation services.";
        public static final String CREATE_OPERATION = "Creates a federation service.";
        public static final String CREATE_REQUEST_BODY = 
                "The body of the request must specify the member ID of the service owner, " + 
                "the service access endpoint, the service metadata map and the discovery and access policies. " +
                "The discovery policy field is the class name of the implementation of ServiceDiscoveryPolicy to be used by the service. " +
                "The access policy field is the class name of the implementation of ServiceAccessPolicy to be used by the service. " +
                "The service metadata map must contain at least the class name of the implementation of ServiceInvoker to be used by the service.";
        public static final String GET_OPERATION = "Lists all services owned by the member.";
        public static final String GET_BY_ID_OPERATION = "Lists a specific service.";
        public static final String DELETE_OPERATION = "Deletes a specific service.";
        public static final String UPDATE_OPERATION = "Updates a specific service.";
        public static final String UPDATE_REQUEST_BODY = 
                "The body of the request must specify the service metadata map and the discovery and access policies. " +
                "The discovery policy field is the class name of the implementation of ServiceDiscoveryPolicy to be used by the service. " +
                "The access policy field is the class name of the implementation of ServiceAccessPolicy to be used by the service. " +
                "The service metadata map must contain at least the class name of the implementation of ServiceInvoker to be used by the service.";
        public static final String SERVICE_ID = "The ID of the specific target service.";
    }
    
    public static class CommonParameters {
        public static final String MEMBER_ID = "The ID of the specific target member.";
    }
    
    public static class Model {
        public static final String ATTRIBUTE_ID = "attribute.id";
        public static final String ATTRIBUTE_NAME = "attribute.name";
        public static final String FEDERATION_NAME = "federation.name";
        public static final String FEDERATION_NAME_NOTE = "(the name of a local federation)";
        public static final String FEDERATION_DESCRIPTION = "federation.description";
        public static final String FEDERATION_METADATA = "{\"property1\": \"value1\", \"property2\": \"value2\"}";
        public static final String FEDERATION_ENABLED = "true";
        public static final String FEDERATION_NUMBER_OF_MEMBERS = "2";
        public static final String FEDERATION_NUMBER_OF_MEMBERS_NOTE = "(the number of members in the specific federation)";
        public static final String FEDERATION_NUMBER_OF_SERVICES = "3";
        public static final String FEDERATION_NUMBER_OF_SERVICES_NOTE = "(the number of services in the specific federation)";
        public static final String OWNER_ID = "owner.id";
        public static final String OWNER_ID_NOTE = "(the ID of a service owner member)";
        public static final String ENDPOINT = "https://service.endpoint";
        public static final String ENDPOINT_NOTE = "(a valid URL used to access the service)";
        public static final String SERVICE_METADATA = 
                "{\"invokerClassName\": \"invoker.class.Name\", \"other.property\": \"other.value\"}";
        public static final String SERVICE_METADATA_NOTE = 
                "(a map containing properties used by the service. Must contain at least the invoker class name)";
        public static final String DISCOVERY_POLICY = "discover.policy.class.Name";
        public static final String DISCOVERY_POLICY_NOTE = "(the class name of the discovery policy to use)";
        public static final String ACCESS_POLICY = "access.policy.class.Name";
        public static final String ACCESS_POLICY_NOTE = "(the class name of the access policy to use)";
        public static final String FEDERATION_USER_NAME = "user.name";
        public static final String FEDERATION_USER_EMAIL = "user@mail.com";
        public static final String FEDERATION_USER_DESCRIPTION = "user description";
        public static final String FEDERATION_USER_ENABLED = "true";
        public static final String FEDERATION_USER_AUTHENTICATION_PROPERTIES = 
                "{\"authentication-service-url\": \"https://auth.service\", \"other.property\": \"other.value\"}";
        public static final String ADMIN_ID = "admin.id";
        public static final String ADMIN_ID_NOTE = "(the ID of a federation admin from a remote FHS)";
        public static final String ADMIN_NAME = "admin.name";
        public static final String ADMIN_EMAIL = "admin.email";
        public static final String ADMIN_DESCRIPTION = "admin.description";
        public static final String ADMIN_ENABLED = "true";
        public static final String CREDENTIALS = "{\"password\":\"pass\", \"other.credential\":\"other.value\"}";
        public static final String FEDERATION_OWNER = "federation.owner.id";
        public static final String FEDERATION_OWNER_NOTE = "(the ID of the federation admin who is the owner of a specific federation)";
        public static final String FEDERATION_ID = "federation.id";
        public static final String FEDERATION_ID_NOTE = "(the ID of a local federation)";
        public static final String MEMBER_ID = "member.id";
        public static final String MEMBER_NAME = "member.name";
        public static final String MEMBER_EMAIL = "member@mail.com";
        public static final String MEMBER_DESCRIPTION = "member.description";
        public static final String MEMBER_ENABLED = "true";
        public static final String MEMBER_ATTRIBUTES = "[\"attribute.id.0\", \"attribute.id.1\"]";
        public static final String MEMBER_ATTRIBUTES_NOTE = "(a list containing the IDs of the attributes of a specific federation member)";
        public static final String FEDERATIONS_OWNED_BY_ADMIN = "[\"federation.id.0\", \"federation.id.1\"]";
        public static final String FEDERATIONS_OWNED_BY_ADMIN_NOTE = "(a list containing the IDs of the federations owned by a specific federation admin)";
        public static final String OPERATION_STR = "get-compute";
        public static final String OPERATION_STR_NOTE = "(a description of the operation to be authorized)";
        public static final String OPERATOR_ID = "operator.id";
        public static final String FHS_ID = "fhs.id";
        public static final String FHS_ID_NOTE = "(the ID of a remote FHS)";
        public static final String PATH = "[\"service.url.part0\", \"service.url.part1\"]";
        public static final String PATH_NOTE = "(a complement of the service endpoint, concatenated to the service endpoint to produce the URL used in the invocation)";
        public static final String HEADERS = "{\"header.key.0\":\"header.value.0\", \"header.key.1\":\"header.value.1\"}";
        public static final String HEADERS_NOTE = "(the headers used in the service invocation)";
        public static final String BODY = "{\"body.key.0\":\"body.value.0\", \"body.key.1\":\"body.value.1\"}";
        public static final String BODY_NOTE = "(the body used in the service invocation)";
        public static final String AUTHORIZED = "true";
        public static final String AUTHORIZED_NOTE = "(a boolean value stating whether the operation is authorized or not)";
        public static final String CLOUD_CREDENTIALS = "{\"credential.key.0\":\"credential.value.0\", \"credential.key.1\":\"credential.value.1\"}";
        public static final String CLOUD_CREDENTIALS_NOTE = "(the credentials used to access a specific cloud provider services)";
        public static final String REQUEST_RESPONSE_CODE = "201";
        public static final String REQUEST_RESPONSE_CODE_NOTE = "(the response code returned by a request to a federation service)";
        public static final String REQUEST_RESPONSE_DATA = "{\"data.key.0\":\"data.value.0\", \"data.key.1\":\"data.value.1\"}";
        public static final String REQUEST_RESPONSE_DATA_NOTE = "(the content returned by a request to a federation service)";
        public static final String SERVICE_ID = "service.id";
        public static final String TOKEN = "token";
        public static final String TOKEN_NOTE = "(the token that identifies a user in the FHS)";
    }
}
