package cloud.fogbow.fhs.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.api.http.response.AllowedRemoteJoin;
import cloud.fogbow.fhs.api.http.response.AttributeDescription;
import cloud.fogbow.fhs.api.http.response.FedAdminInfo;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationInfo;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.api.parameters.OperationToAuthorize;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.RemoteFederation;
import cloud.fogbow.fhs.core.models.RemoteFederationUser;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;
import cloud.fogbow.fhs.core.utils.SynchronizationManager;

// TODO refactor
// TODO add checks to authorization
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FhsPublicKeysHolder.class , AuthenticationUtil.class, 
    ServiceAsymmetricKeysHolder.class, PropertiesHolder.class,
    AuthorizationPluginInstantiator.class })
public class ApplicationFacadeTest {
    private static final String TOKEN_1 = "userToken";
    private static final String TOKEN_2 = "userToken2";
    private static final String ADMIN_ID_1 = "adminId";
    private static final String ADMIN_NAME_1 = "adminName";
    private static final String ADMIN_NAME_2 = "adminName2";
    private static final String ADMIN_EMAIL_1 = "adminEmail";
    private static final String ADMIN_DESCRIPTION_1 = "adminDescription";
    private static final String ADMIN_ID_2 = "adminId2";
    private static final String ADMIN_DESCRIPTION_2 = "adminDescription2";
    private static final String ADMIN_EMAIL_2 = "adminEmail2";
    private static final boolean ADMIN_ENABLED_2 = true;
    private static final boolean ADMIN_ENABLED = true;
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_NAME_2 = "federationName2";
    private static final Map<String, String> FEDERATION_METADATA_1 = new HashMap<String, String>();
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final String FEDERATION_DESCRIPTION_2 = "federationDescription2";
    private static final boolean FEDERATION_ENABLED_1 = true;
    private static final Boolean FEDERATION_ENABLED_2 = true;
    private static final String MEMBER_ID_1 = "memberId1";
    private static final String MEMBER_ID_2 = "memberId2";
    private static final String USER_ID_TO_ADD = "userToAdd";
    private static final String USER_EMAIL_TO_ADD = "userEmailToAdd";
    private static final String USER_DESCRIPTION_TO_ADD = "userDescriptionToAdd";
    private static final String USER_ID_1 = "userId1";
    private static final String USER_ID_2 = "userId2";
    private static final String USER_DESCRIPTION_1 = "userDescription1";
    private static final String USER_DESCRIPTION_2 = "userDescription2";
    private static final String USER_EMAIL_1 = "userEmail1";
    private static final String USER_EMAIL_2 = "userEmail2";
    private static final Boolean USER_ENABLED_1 = true;
    private static final Boolean USER_ENABLED_2 = true;
    private static final String SERVICE_ID_1 = "serviceId";
    private static final String SERVICE_ID_2 = "serviceId2";
    private static final String SERVICE_ENDPOINT_1 = "serviceEndpoint";
    private static final String SERVICE_ENDPOINT_2 = "serviceEndpoint2";
    private static final Map<String, String> SERVICE_METADATA_1 = new HashMap<String, String>();
    private static final Map<String, String> SERVICE_METADATA_2 = new HashMap<String, String>();
    private static final String SERVICE_DISCOVERY_POLICY_CLASS_NAME = "discoveryPolicy";
    private static final String SERVICE_ACCESS_POLICY_CLASS_NAME = "accessPolicy";
    private static final Integer RESPONSE_CODE = 200;
    private static final List<String> PATH = new ArrayList<String>();
    private static final Map<String, String> HEADERS = new HashMap<String, String>();
    private static final Map<String, Object> BODY = new HashMap<String, Object>();
    private static final Map<String, String> RESPONSE_DATA = new HashMap<String, String>();
    private static final String CLOUD_NAME = "cloudName";
    private static final Map<String, String> CREDENTIALS = new HashMap<String, String>();
    private static final String ATTRIBUTE_ID_1 = "attributeId1";
    private static final String ATTRIBUTE_ID_2 = "attributeId2";
    private static final String ATTRIBUTE_NAME_1 = "attributeName1";
    private static final String ATTRIBUTE_NAME_2 = "attributeName2";
    private static final Map<String, String> ADMIN_AUTHORIZATION_PROPERTIES = new HashMap<String, String>();
    private static final Map<String, String> USER_AUTHORIZATION_PROPERTIES = new HashMap<String, String>();
    private static final String FHS_OPERATOR_ID_1 = "fhsOperatorId1";
    private static final String FHS_OPERATOR_NAME_1 = "fhsOperatorName1";
    private static final String FHS_OPERATOR_DESCRIPTION_1 = "fhsOperatorDescription1";
    private static final String FHS_OPERATOR_EMAIL_1 = "fhsOperatorEmail1";
    private static final boolean FHS_OPERATOR_ENABLED_1 = true;
    private static final String FHS_OPERATOR_ID_2 = "fhsOperatorId2";
    private static final String FHS_OPERATOR_DESCRIPTION_2 = "fhsOperatorDescription2";
    private static final String FHS_OPERATOR_EMAIL_2 = "fhsOperatorEmail2";
    private static final boolean FHS_OPERATOR_ENABLED_2 = true;
    private static final String AUTHENTICATION_PLUGIN_CLASS_NAME = "authenticationPluginClassName";
    private static final Map<String, String> AUTHENTICATION_PLUGIN_PROPERTIES = new HashMap<String, String>();
    private static final String FHS_OPERATOR_TOKEN_1 = "fhsOperatorToken1";
    private static final String FHS_OPERATOR_TOKEN_2 = "fhsOperatorToken2";
    private static final String USER_TOKEN_1 = "userToken1";
    private static final String ADMIN_TOKEN = "adminToken";
    private static final String FHS_OPERATOR_1_CREDENTIAL_KEY_1 = "fhsOperator1CredentialKey1";
    private static final String FHS_OPERATOR_1_CREDENTIAL_KEY_2 = "fhsOperator1CredentialKey2";
    private static final String FHS_OPERATOR_2_CREDENTIAL_KEY_1 = "fhsOperator2CredentialKey1";
    private static final String FHS_OPERATOR_2_CREDENTIAL_KEY_2 = "fhsOperator2CredentialKey2";
    private static final String ADMIN_CREDENTIAL_KEY_1 = "adminCredentialKey1";
    private static final String ADMIN_CREDENTIAL_KEY_2 = "adminCredentialKey2";
    private static final String FHS_OPERATOR_1_CREDENTIAL_VALUE_1 = "fhsOperator1CredentialValue1";
    private static final String FHS_OPERATOR_1_CREDENTIAL_VALUE_2 = "fhsOperator1CredentialValue2";
    private static final String FHS_OPERATOR_2_CREDENTIAL_VALUE_1 = "fhsOperator2CredentialValue1";
    private static final String FHS_OPERATOR_2_CREDENTIAL_VALUE_2 = "fhsOperator2CredentialValue2";
    private static final String ADMIN_CREDENTIAL_VALUE_1 = "adminCredentialValue1";
    private static final String ADMIN_CREDENTIAL_VALUE_2 = "adminCredentialValue2";
    
    private static final String OPERATOR_ID_1 = "operator1";
    private static final String OPERATOR_ID_2 = "operator2";
    private static final String OPERATOR_ID_3 = "operator3";
    private static final String OPERATOR_IDS = 
            String.join(SystemConstants.OPERATOR_IDS_SEPARATOR, OPERATOR_ID_1, OPERATOR_ID_2, OPERATOR_ID_3);
    private static final String RELOAD_OPERATOR_ID_1 = "reloadOperator1";
    private static final String RELOAD_OPERATOR_ID_2 = "reloadOperator2";
    private static final String RELOAD_OPERATOR_ID_3 = "reloadOperator3";
    private static final String RELOAD_OPERATOR_IDS = 
            String.join(SystemConstants.OPERATOR_IDS_SEPARATOR, RELOAD_OPERATOR_ID_1, RELOAD_OPERATOR_ID_2, RELOAD_OPERATOR_ID_3);
    private static final String PROPERTY_1 = "identityPluginClassName";
    private static final String PROPERTY_2 = "property2";
    private static final String PROPERTY_3 = "property3";
    private static final String OPERATOR_1_PROPERTY_1_KEY = OPERATOR_ID_1 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_1_PROPERTY_2_KEY = OPERATOR_ID_1 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_1_PROPERTY_3_KEY = OPERATOR_ID_1 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String OPERATOR_2_PROPERTY_1_KEY = OPERATOR_ID_2 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_2_PROPERTY_2_KEY = OPERATOR_ID_2 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_2_PROPERTY_3_KEY = OPERATOR_ID_2 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String OPERATOR_3_PROPERTY_1_KEY = OPERATOR_ID_3 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_1;
    private static final String OPERATOR_3_PROPERTY_2_KEY = OPERATOR_ID_3 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_2;
    private static final String OPERATOR_3_PROPERTY_3_KEY = OPERATOR_ID_3 + ApplicationFacade.PROPERTY_NAME_OPERATOR_ID_SEPARATOR + PROPERTY_3;
    private static final String NOT_OPERATOR_PROPERTY_KEY = "not_operator_property_key";
    private static final String OPERATOR_1_PROPERTY_1_VALUE = "operator1_property1";
    private static final String OPERATOR_1_PROPERTY_2_VALUE = "operator1_property2";
    private static final String OPERATOR_1_PROPERTY_3_VALUE = "operator1_property3";
    private static final String OPERATOR_2_PROPERTY_1_VALUE = "operator2_property1";
    private static final String OPERATOR_2_PROPERTY_2_VALUE = "operator2_property2";
    private static final String OPERATOR_2_PROPERTY_3_VALUE = "operator2_property3";
    private static final String OPERATOR_3_PROPERTY_1_VALUE = "operator3_property1";
    private static final String OPERATOR_3_PROPERTY_2_VALUE = "operator3_property2";
    private static final String OPERATOR_3_PROPERTY_3_VALUE = "operator3_property3";
    private static final String NOT_OPERATOR_PROPERTY_VALUE = "not_operator_property_value";
    private static final String REMOTE_FEDERATION_ID_1 = "remoteFederationId1";
    private static final String REMOTE_FEDERATION_ID_2 = "remoteFederationId2";
    private static final String REMOTE_FEDERATION_NAME_1 = "remoteFederationName1";
    private static final String REMOTE_FEDERATION_NAME_2 = "remoteFederationName2";
    private static final String REMOTE_OWNING_FED_ADMIN_ID_1 = "remoteOwningFedAdminId1";
    private static final String REMOTE_OWNING_FED_ADMIN_ID_2 = "remoteOwningFedAdminId2";
    private static final boolean REMOTE_FEDERATION_ENABLED_1 = true;
    private static final boolean REMOTE_FEDERATION_ENABLED_2 = true;
    private static final String REMOTE_OWNER_FHS_ID_1 = "remoteOwnerFhsId1";
    private static final String REMOTE_OWNER_FHS_ID_2 = "remoteOwnerFhsId2";
    private static final String REMOTE_FEDERATION_DESCRIPTION_1 = "remoteFederationDescription1";
    private static final String REMOTE_FEDERATION_DESCRIPTION_2 = "remoteFederationDescription2";
    private static final String REMOTE_FEDERATION_ADMIN_ID_1 = "remoteFederationAdminId1";
    private static final String REMOTE_FEDERATION_ADMIN_ID_2 = "remoteFederationAdminId2";
    private static final String REMOTE_FHS_ID_1 = "remoteFhsId1";
    private static final String AUTHORIZATION_PLUGIN_CLASS_NAME = "authorizationPluginClassName";
    private static final String PUBLIC_KEY_FILE_PATH = "publicKeyFilePath";
    private static final String PRIVATE_KEY_FILE_PATH = "privateKeyFilePath";
    private static final String OPERATION_STR = "operationStr";
    
    private ApplicationFacade applicationFacade;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    private RSAPublicKey asPublicKey;
    private Federation federation1;
    private Federation federation2;
    private Federation federationOwnedByAnotherFhs1;
    private Federation federationOwnedByAnotherFhs2;
    private SystemUser systemUser1;
    private SystemUser systemUser2;
    private SystemUser fhsOperatorSystemUser1;
    private FederationUser federationUser1;
    private FederationUser federationUser2;
    private FederationUser fhsOperator1;
    private FederationUser fhsOperator2;
    private FederationUser federationAdmin1;
    private FederationUser federationAdmin2;
    private List<FederationUser> fhsOperators;
    private Map<String, String> fhsOperatorCredentials1;
    private Map<String, String> fhsOperatorCredentials2;
    private Map<String, String> userCredentials1;
    private Map<String, String> adminCredentials;
    private FederationService federationService;
    private FederationService federationService2; 
    private ServiceDiscoveryPolicy serviceDiscoveryPolicy;
    private ServiceInvoker serviceInvoker;
    private FederationAttribute federationAttribute1;
    private FederationAttribute federationAttribute2;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationAuthenticationPlugin authenticationPlugin;
    private SynchronizationManager synchronizationManager;
    private Properties properties;
    private PropertiesHolder propertiesHolder;
    private RemoteFederation remoteFederation1;
    private RemoteFederation remoteFederation2;
    private AuthorizationPlugin<FhsOperation> newAuthorizationPlugin;
    private RemoteFederationUser remoteFederationUser1;
    private RemoteFederationUser remoteFederationUser2;
    private OperationToAuthorize operationToAuthorize;
    
    @Before
    public void setUp() throws FogbowException {
        HashSet<Object> propertiesKeySet = new HashSet<Object>();
        propertiesKeySet.add(OPERATOR_1_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_1_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_1_PROPERTY_3_KEY);
        propertiesKeySet.add(NOT_OPERATOR_PROPERTY_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_2_PROPERTY_3_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_1_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_2_KEY);
        propertiesKeySet.add(OPERATOR_3_PROPERTY_3_KEY);
        
        this.properties = Mockito.mock(Properties.class);
        Mockito.when(this.properties.keySet()).thenReturn(propertiesKeySet);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_1_KEY)).thenReturn(OPERATOR_1_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_2_KEY)).thenReturn(OPERATOR_1_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_1_PROPERTY_3_KEY)).thenReturn(OPERATOR_1_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_1_KEY)).thenReturn(OPERATOR_2_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_2_KEY)).thenReturn(OPERATOR_2_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_2_PROPERTY_3_KEY)).thenReturn(OPERATOR_2_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_1_KEY)).thenReturn(OPERATOR_3_PROPERTY_1_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_2_KEY)).thenReturn(OPERATOR_3_PROPERTY_2_VALUE);
        Mockito.when(this.properties.getProperty(OPERATOR_3_PROPERTY_3_KEY)).thenReturn(OPERATOR_3_PROPERTY_3_VALUE);
        Mockito.when(this.properties.getProperty(NOT_OPERATOR_PROPERTY_KEY)).thenReturn(NOT_OPERATOR_PROPERTY_VALUE);
        
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(OPERATOR_IDS);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY)).thenReturn(AUTHORIZATION_PLUGIN_CLASS_NAME);
        Mockito.when(this.propertiesHolder.getProperties()).thenReturn(properties);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        synchronizationManager = Mockito.mock(SynchronizationManager.class);
        
        serviceDiscoveryPolicy = Mockito.mock(ServiceDiscoveryPolicy.class);
        Mockito.when(serviceDiscoveryPolicy.getName()).thenReturn(SERVICE_DISCOVERY_POLICY_CLASS_NAME);
        
        serviceInvoker = Mockito.mock(ServiceInvoker.class);
        Mockito.when(serviceInvoker.getName()).thenReturn(SERVICE_ACCESS_POLICY_CLASS_NAME);
        
        federationService = Mockito.mock(FederationService.class);
        Mockito.when(federationService.getServiceId()).thenReturn(SERVICE_ID_1);
        Mockito.when(federationService.getEndpoint()).thenReturn(SERVICE_ENDPOINT_1);
        Mockito.when(federationService.getMetadata()).thenReturn(SERVICE_METADATA_1);
        Mockito.when(federationService.getDiscoveryPolicy()).thenReturn(serviceDiscoveryPolicy);
        Mockito.when(federationService.getInvoker()).thenReturn(serviceInvoker);
        
        federationService2 = Mockito.mock(FederationService.class);
        Mockito.when(federationService2.getServiceId()).thenReturn(SERVICE_ID_2);
        Mockito.when(federationService2.getEndpoint()).thenReturn(SERVICE_ENDPOINT_2);
        Mockito.when(federationService2.getMetadata()).thenReturn(SERVICE_METADATA_2);
        Mockito.when(federationService2.getDiscoveryPolicy()).thenReturn(serviceDiscoveryPolicy);
        Mockito.when(federationService2.getInvoker()).thenReturn(serviceInvoker);
        
        federationUser1 = Mockito.mock(FederationUser.class);
        Mockito.when(federationUser1.getMemberId()).thenReturn(MEMBER_ID_1);
        Mockito.when(federationUser1.getName()).thenReturn(USER_ID_TO_ADD);
        Mockito.when(federationUser1.getDescription()).thenReturn(USER_DESCRIPTION_1);
        Mockito.when(federationUser1.getEmail()).thenReturn(USER_EMAIL_1);
        Mockito.when(federationUser1.getAttributes()).thenReturn(Arrays.asList(ATTRIBUTE_NAME_1, ATTRIBUTE_NAME_2));
        Mockito.when(federationUser1.isEnabled()).thenReturn(USER_ENABLED_1);
        
        federationUser2 = Mockito.mock(FederationUser.class);
        Mockito.when(federationUser2.getMemberId()).thenReturn(MEMBER_ID_2);
        Mockito.when(federationUser2.getName()).thenReturn(USER_ID_2);
        Mockito.when(federationUser2.getDescription()).thenReturn(USER_DESCRIPTION_2);
        Mockito.when(federationUser2.getEmail()).thenReturn(USER_EMAIL_2);
        Mockito.when(federationUser2.isEnabled()).thenReturn(USER_ENABLED_2);
        
        federationAdmin1 = Mockito.mock(FederationUser.class);
        Mockito.when(federationAdmin1.getMemberId()).thenReturn(ADMIN_ID_1);
        Mockito.when(federationAdmin1.getName()).thenReturn(ADMIN_NAME_1);
        Mockito.when(federationAdmin1.getDescription()).thenReturn(ADMIN_DESCRIPTION_1);
        Mockito.when(federationAdmin1.getEmail()).thenReturn(ADMIN_EMAIL_1);
        Mockito.when(federationAdmin1.isEnabled()).thenReturn(ADMIN_ENABLED);
        
        federationAdmin2 = Mockito.mock(FederationUser.class);
        Mockito.when(federationAdmin2.getMemberId()).thenReturn(ADMIN_ID_2);
        Mockito.when(federationAdmin2.getName()).thenReturn(ADMIN_NAME_2);
        Mockito.when(federationAdmin2.getDescription()).thenReturn(ADMIN_DESCRIPTION_2);
        Mockito.when(federationAdmin2.getEmail()).thenReturn(ADMIN_EMAIL_2);
        Mockito.when(federationAdmin2.isEnabled()).thenReturn(ADMIN_ENABLED_2);
        
        fhsOperator1 = Mockito.mock(FederationUser.class);
        Mockito.when(fhsOperator1.getMemberId()).thenReturn(FHS_OPERATOR_ID_1);
        Mockito.when(fhsOperator1.getName()).thenReturn(FHS_OPERATOR_ID_1);
        Mockito.when(fhsOperator1.getDescription()).thenReturn(FHS_OPERATOR_DESCRIPTION_1);
        Mockito.when(fhsOperator1.getEmail()).thenReturn(FHS_OPERATOR_EMAIL_1);
        Mockito.when(fhsOperator1.isEnabled()).thenReturn(FHS_OPERATOR_ENABLED_1);
        Mockito.when(fhsOperator1.getIdentityPluginClassName()).thenReturn(AUTHENTICATION_PLUGIN_CLASS_NAME);
        Mockito.when(fhsOperator1.getIdentityPluginProperties()).thenReturn(AUTHENTICATION_PLUGIN_PROPERTIES);
        
        fhsOperator2 = Mockito.mock(FederationUser.class);
        Mockito.when(fhsOperator2.getMemberId()).thenReturn(FHS_OPERATOR_ID_2);
        Mockito.when(fhsOperator2.getName()).thenReturn(FHS_OPERATOR_ID_2);
        Mockito.when(fhsOperator2.getDescription()).thenReturn(FHS_OPERATOR_DESCRIPTION_2);
        Mockito.when(fhsOperator2.getEmail()).thenReturn(FHS_OPERATOR_EMAIL_2);
        Mockito.when(fhsOperator2.isEnabled()).thenReturn(FHS_OPERATOR_ENABLED_2);
        Mockito.when(fhsOperator2.getIdentityPluginClassName()).thenReturn(AUTHENTICATION_PLUGIN_CLASS_NAME);
        Mockito.when(fhsOperator2.getIdentityPluginProperties()).thenReturn(AUTHENTICATION_PLUGIN_PROPERTIES);
        
        remoteFederationUser1 = new RemoteFederationUser(REMOTE_FEDERATION_ADMIN_ID_1, REMOTE_FHS_ID_1);
        remoteFederationUser2 = new RemoteFederationUser(REMOTE_FEDERATION_ADMIN_ID_2, REMOTE_FHS_ID_1);
        
        federation1 = Mockito.mock(Federation.class);
        Mockito.when(federation1.getName()).thenReturn(FEDERATION_NAME_1);
        Mockito.when(federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(federation1.getDescription()).thenReturn(FEDERATION_DESCRIPTION_1);
        Mockito.when(federation1.getOwner()).thenReturn(ADMIN_ID_1);
        Mockito.when(federation1.getMemberList()).thenReturn(Arrays.asList(federationUser1, federationUser2));
        Mockito.when(federation1.getServices()).thenReturn(Arrays.asList(federationService, federationService2));
        Mockito.when(federation1.enabled()).thenReturn(FEDERATION_ENABLED_1);
        Mockito.when(federation1.getAllowedRemoteJoins()).thenReturn(Arrays.asList(remoteFederationUser1, remoteFederationUser2));
        
        federation2 = Mockito.mock(Federation.class);
        Mockito.when(federation2.getName()).thenReturn(FEDERATION_NAME_2);
        Mockito.when(federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(federation2.getDescription()).thenReturn(FEDERATION_DESCRIPTION_2);
        Mockito.when(federation2.getOwner()).thenReturn(ADMIN_ID_1);
        Mockito.when(federation2.enabled()).thenReturn(FEDERATION_ENABLED_2);
        Mockito.when(federation2.getAllowedRemoteJoins()).thenReturn(Arrays.asList(remoteFederationUser1));
        
        federationOwnedByAnotherFhs1 = Mockito.mock(Federation.class);
        Mockito.when(federationOwnedByAnotherFhs1.getName()).thenReturn(REMOTE_FEDERATION_NAME_1);
        Mockito.when(federationOwnedByAnotherFhs1.getId()).thenReturn(REMOTE_FEDERATION_ID_1);
        Mockito.when(federationOwnedByAnotherFhs1.getDescription()).thenReturn(REMOTE_FEDERATION_DESCRIPTION_1);
        
        federationOwnedByAnotherFhs2 = Mockito.mock(Federation.class);
        Mockito.when(federationOwnedByAnotherFhs2.getName()).thenReturn(REMOTE_FEDERATION_NAME_2);
        Mockito.when(federationOwnedByAnotherFhs2.getId()).thenReturn(REMOTE_FEDERATION_ID_2);
        Mockito.when(federationOwnedByAnotherFhs2.getDescription()).thenReturn(REMOTE_FEDERATION_DESCRIPTION_2);
        
        remoteFederation1 = new RemoteFederation(REMOTE_FEDERATION_ID_1, REMOTE_FEDERATION_NAME_1, 
                REMOTE_FEDERATION_DESCRIPTION_1, REMOTE_FEDERATION_ENABLED_1, 
                REMOTE_OWNING_FED_ADMIN_ID_1, REMOTE_OWNER_FHS_ID_1);
        remoteFederation2 = new RemoteFederation(REMOTE_FEDERATION_ID_2, REMOTE_FEDERATION_NAME_2, 
                REMOTE_FEDERATION_DESCRIPTION_2, REMOTE_FEDERATION_ENABLED_2, 
                REMOTE_OWNING_FED_ADMIN_ID_2, REMOTE_OWNER_FHS_ID_2);
        
        federationAttribute1 = Mockito.mock(FederationAttribute.class);
        Mockito.when(federationAttribute1.getId()).thenReturn(ATTRIBUTE_ID_1);
        Mockito.when(federationAttribute1.getName()).thenReturn(ATTRIBUTE_NAME_1);
        
        federationAttribute2 = Mockito.mock(FederationAttribute.class);
        Mockito.when(federationAttribute2.getId()).thenReturn(ATTRIBUTE_ID_2);
        Mockito.when(federationAttribute2.getName()).thenReturn(ATTRIBUTE_NAME_2);
        
        asPublicKey = Mockito.mock(RSAPublicKey.class);
        
        systemUser1 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser1.getId()).thenReturn(ADMIN_NAME_1);
        systemUser2 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser2.getId()).thenReturn(ADMIN_NAME_2);
        fhsOperatorSystemUser1 = Mockito.mock(SystemUser.class);
        Mockito.when(fhsOperatorSystemUser1.getId()).thenReturn(FHS_OPERATOR_NAME_1);
        
        ServiceAsymmetricKeysHolder keysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(keysHolder.getPublicKey()).thenReturn(asPublicKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(keysHolder);
        
        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1)).willReturn(systemUser1);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_2)).willReturn(systemUser2);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1)).willReturn(fhsOperatorSystemUser1);
        
        this.fhsOperatorCredentials1 = new HashMap<String, String>();
        this.fhsOperatorCredentials1.put(FHS_OPERATOR_1_CREDENTIAL_KEY_1, FHS_OPERATOR_1_CREDENTIAL_VALUE_1);
        this.fhsOperatorCredentials1.put(FHS_OPERATOR_1_CREDENTIAL_KEY_2, FHS_OPERATOR_1_CREDENTIAL_VALUE_2);
        
        this.fhsOperatorCredentials2 = new HashMap<String, String>();
        this.fhsOperatorCredentials2.put(FHS_OPERATOR_2_CREDENTIAL_KEY_1, FHS_OPERATOR_2_CREDENTIAL_VALUE_1);
        this.fhsOperatorCredentials2.put(FHS_OPERATOR_2_CREDENTIAL_KEY_2, FHS_OPERATOR_2_CREDENTIAL_VALUE_2);
        
        this.adminCredentials = new HashMap<String, String>();
        this.adminCredentials.put(ADMIN_CREDENTIAL_KEY_1, ADMIN_CREDENTIAL_VALUE_1);
        this.adminCredentials.put(ADMIN_CREDENTIAL_KEY_2, ADMIN_CREDENTIAL_VALUE_2);
        
        this.userCredentials1 = new HashMap<String, String>();
        
        this.authenticationPlugin = Mockito.mock(FederationAuthenticationPlugin.class);
        Mockito.when(this.authenticationPlugin.authenticate(fhsOperatorCredentials1)).thenReturn(FHS_OPERATOR_TOKEN_1);
        Mockito.when(this.authenticationPlugin.authenticate(fhsOperatorCredentials2)).thenReturn(FHS_OPERATOR_TOKEN_2);
        Mockito.when(this.authenticationPlugin.authenticate(userCredentials1)).thenReturn(USER_TOKEN_1);
        
        this.authenticationPluginInstantiator = Mockito.mock(FederationAuthenticationPluginInstantiator.class);
        Mockito.when(this.authenticationPluginInstantiator.getAuthenticationPlugin(
                AUTHENTICATION_PLUGIN_CLASS_NAME, AUTHENTICATION_PLUGIN_PROPERTIES)).thenReturn(authenticationPlugin);
        
        this.operationToAuthorize = new OperationToAuthorize(OPERATION_STR);
        
        this.authorizationPlugin = Mockito.mock(AuthorizationPlugin.class);
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.addFederationAdmin(ADMIN_NAME_1, ADMIN_EMAIL_1, 
                ADMIN_DESCRIPTION_1, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES)).thenReturn(ADMIN_ID_1);
        Mockito.when(this.federationHost.getFederationAdmins()).thenReturn(Arrays.asList(federationAdmin1, federationAdmin2));
        Mockito.when(this.federationHost.getFederations()).thenReturn(Arrays.asList(federation1, federation2));
        Mockito.when(this.federationHost.getFederationsInstancesOwnedByAnotherMember(ADMIN_NAME_1)).
                thenReturn(Arrays.asList(federation1, federation2));
        Mockito.when(this.federationHost.createFederation(ADMIN_NAME_1, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1)).thenReturn(federation1);
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_1)).
                thenReturn(Arrays.asList(federation1, federation2));
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_2)).thenReturn(Arrays.asList());
        Mockito.when(this.federationHost.getAdminRemoteFederations(ADMIN_NAME_1)).thenReturn(
                Arrays.asList(federationOwnedByAnotherFhs1, federationOwnedByAnotherFhs2));
        Mockito.when(this.federationHost.getFederation(ADMIN_NAME_1, FEDERATION_ID_1)).thenReturn(federation1);
        Mockito.when(this.federationHost.getRemoteFederationList(ADMIN_NAME_1)).thenReturn(
                Arrays.asList(remoteFederation1, remoteFederation2));
        Mockito.when(this.federationHost.grantMembership(ADMIN_NAME_1, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD,
                USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES)).thenReturn(federationUser1);
        Mockito.when(this.federationHost.getFederationMembers(ADMIN_NAME_1, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationUser1, federationUser2));
        Mockito.when(this.federationHost.getFederationMemberInfo(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1)).thenReturn(federationUser1);
        Mockito.when(this.federationHost.createAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1)).thenReturn(ATTRIBUTE_ID_1);
        Mockito.when(this.federationHost.getFederationAttributes(ADMIN_NAME_1, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationAttribute1, federationAttribute2));
        Mockito.when(this.federationHost.registerService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME)).thenReturn(SERVICE_ID_1);
        Mockito.when(this.federationHost.getOwnedServices(ADMIN_NAME_1, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(SERVICE_ID_1, SERVICE_ID_2));
        Mockito.when(this.federationHost.getOwnedService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ID_1)).
                thenReturn(federationService);
        Mockito.when(this.federationHost.getAuthorizedServices(ADMIN_NAME_1, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationService, federationService2));
        Mockito.when(this.federationHost.invokeService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ID_1, 
                HttpMethod.GET, PATH, HEADERS, BODY)).thenReturn(new DefaultServiceResponse(RESPONSE_CODE, RESPONSE_DATA));
        Mockito.when(this.federationHost.map(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME)).thenReturn(CREDENTIALS);
        Mockito.when(this.federationHost.login("", USER_ID_1, userCredentials1)).thenReturn(USER_TOKEN_1);
        Mockito.when(this.federationHost.federationAdminLogin(ADMIN_ID_1, adminCredentials)).thenReturn(ADMIN_TOKEN);
        Mockito.when(this.federationHost.isAuthorized(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, OPERATION_STR)).thenReturn(true);
        
        this.fhsOperators = new ArrayList<FederationUser>();
        this.fhsOperators.add(fhsOperator1);
        this.fhsOperators.add(fhsOperator2);
        
        applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
        applicationFacade.setFhsOperators(fhsOperators);
        applicationFacade.setAuthenticationPluginInstantiator(authenticationPluginInstantiator);
        applicationFacade.setSynchronizationManager(synchronizationManager);
    }

    @Test
    public void testLoadFhsOperators() throws ConfigurationErrorException {
        List<FederationUser> fhsOperators = ApplicationFacade.loadFhsOperatorsOrFail();
        
        assertEquals(3, fhsOperators.size());
        
        FederationUser operator1 = fhsOperators.get(0);
        Map<String, String> operator1Properties = operator1.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_1, operator1.getName());
        assertEquals(OPERATOR_1_PROPERTY_1_VALUE, operator1Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_1_PROPERTY_2_VALUE, operator1Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_1_PROPERTY_3_VALUE, operator1Properties.get(PROPERTY_3));
        
        FederationUser operator2 = fhsOperators.get(1);
        Map<String, String> operator2Properties = operator2.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_2, operator2.getName());
        assertEquals(OPERATOR_2_PROPERTY_1_VALUE, operator2Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_2_PROPERTY_2_VALUE, operator2Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_2_PROPERTY_3_VALUE, operator2Properties.get(PROPERTY_3));
        
        FederationUser operator3 = fhsOperators.get(2);
        Map<String, String> operator3Properties = operator3.getIdentityPluginProperties();
        assertEquals(OPERATOR_ID_3, operator3.getName());
        assertEquals(OPERATOR_3_PROPERTY_1_VALUE, operator3Properties.get(PROPERTY_1));
        assertEquals(OPERATOR_3_PROPERTY_2_VALUE, operator3Properties.get(PROPERTY_2));
        assertEquals(OPERATOR_3_PROPERTY_3_VALUE, operator3Properties.get(PROPERTY_3));
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testLoadFhsOperatorsEmptyOperatorsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn("");
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        ApplicationFacade.loadFhsOperatorsOrFail();
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testLoadFhsOperatorsNullOperatorsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(null);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        ApplicationFacade.loadFhsOperatorsOrFail();
    }
    
    @Test
    public void testAddFederationAdmin() throws FogbowException {
        String returnedAdminId = this.applicationFacade.addFederationAdmin(FHS_OPERATOR_TOKEN_1, ADMIN_NAME_1, ADMIN_EMAIL_1, 
                ADMIN_DESCRIPTION_1, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES);
        
        assertEquals(ADMIN_ID_1, returnedAdminId);
        
        Mockito.verify(this.federationHost).addFederationAdmin(ADMIN_NAME_1, ADMIN_EMAIL_1, 
                ADMIN_DESCRIPTION_1, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES);
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testGetFederationAdmins() throws FogbowException {
        List<FedAdminInfo> adminsInfo = this.applicationFacade.getFederationAdmins(FHS_OPERATOR_TOKEN_1);
        
        assertEquals(2, adminsInfo.size());
        
        FedAdminInfo infoAdmin1 = adminsInfo.get(0);
        
        assertEquals(ADMIN_ID_1, infoAdmin1.getMemberId());
        assertEquals(ADMIN_NAME_1, infoAdmin1.getMemberName());
        assertEquals(ADMIN_DESCRIPTION_1, infoAdmin1.getDescription());
        assertEquals(ADMIN_EMAIL_1, infoAdmin1.getEmail());
        assertTrue(infoAdmin1.getEnabled());
        assertEquals(2, infoAdmin1.getFedsOwned().size());
        assertTrue(infoAdmin1.getFedsOwned().contains(FEDERATION_ID_1));
        assertTrue(infoAdmin1.getFedsOwned().contains(FEDERATION_ID_2));
        
        FedAdminInfo infoAdmin2 = adminsInfo.get(1);
        
        assertEquals(ADMIN_ID_2, infoAdmin2.getMemberId());
        assertEquals(ADMIN_NAME_2, infoAdmin2.getMemberName());
        assertEquals(ADMIN_DESCRIPTION_2, infoAdmin2.getDescription());
        assertEquals(ADMIN_EMAIL_2, infoAdmin2.getEmail());
        assertTrue(infoAdmin2.getEnabled());
        assertTrue(infoAdmin2.getFedsOwned().isEmpty());
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testUpdateFederationAdmin() throws FogbowException {
        this.applicationFacade.updateFederationAdmin(FHS_OPERATOR_TOKEN_1, ADMIN_ID_1, ADMIN_NAME_1, 
                ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED);
        
        Mockito.verify(this.federationHost).updateFederationAdmin(ADMIN_ID_1, ADMIN_NAME_1, 
                ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED);
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testDeleteFederationAdmin() throws FogbowException {
        this.applicationFacade.deleteFederationAdmin(FHS_OPERATOR_TOKEN_1, ADMIN_ID_1);
        
        Mockito.verify(this.federationHost).deleteFederationAdmin(ADMIN_ID_1);
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testListFederationInstances() throws FogbowException {
        List<FederationInstance> instances = this.applicationFacade.listFederationInstances(FHS_OPERATOR_TOKEN_1);
        
        assertEquals(2, instances.size());
        assertEquals(FEDERATION_ID_1, instances.get(0).getFedId());
        assertEquals(FEDERATION_NAME_1, instances.get(0).getFedName());
        assertEquals(FEDERATION_DESCRIPTION_1, instances.get(0).getDescription());
        assertEquals(ADMIN_ID_1, instances.get(0).getOwningFedAdminId());
        assertEquals(FEDERATION_ID_2, instances.get(1).getFedId());
        assertEquals(FEDERATION_NAME_2, instances.get(1).getFedName());
        assertEquals(FEDERATION_DESCRIPTION_2, instances.get(1).getDescription());
        assertEquals(ADMIN_ID_1, instances.get(1).getOwningFedAdminId());
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testUpdateFederation() throws FogbowException {
        this.applicationFacade.updateFederation(FHS_OPERATOR_TOKEN_1, FEDERATION_ID_1, FEDERATION_ENABLED_1);
        
        Mockito.verify(this.federationHost).updateFederation(FEDERATION_ID_1, FEDERATION_ENABLED_1);
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testDeleteFederationInstance() throws FogbowException {
        this.applicationFacade.deleteFederationInstance(FHS_OPERATOR_TOKEN_1, FEDERATION_ID_1);
        
        Mockito.verify(this.federationHost).deleteFederationInstance(FEDERATION_ID_1);
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
    }
    
    @Test
    public void testReload() throws FogbowException {
        this.newAuthorizationPlugin = Mockito.mock(AuthorizationPlugin.class);
        DatabaseManager databaseManager = Mockito.mock(DatabaseManager.class);
        FhsCommunicationMechanism communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        
        PowerMockito.mockStatic(FhsPublicKeysHolder.class);
        
        PowerMockito.mockStatic(AuthorizationPluginInstantiator.class);
        BDDMockito.given(AuthorizationPluginInstantiator.getAuthorizationPlugin(AUTHORIZATION_PLUGIN_CLASS_NAME)).
            willReturn(newAuthorizationPlugin);
        
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(RELOAD_OPERATOR_IDS);
        Mockito.when(this.propertiesHolder.getProperty(FogbowConstants.PUBLIC_KEY_FILE_PATH)).thenReturn(PUBLIC_KEY_FILE_PATH);
        Mockito.when(this.propertiesHolder.getProperty(FogbowConstants.PRIVATE_KEY_FILE_PATH)).thenReturn(PRIVATE_KEY_FILE_PATH);
        
        this.applicationFacade.setDatabaseManager(databaseManager);
        this.applicationFacade.setFhsCommunicationMechanism(communicationMechanism);
        
        this.applicationFacade.reload(FHS_OPERATOR_TOKEN_1);
        
        BDDMockito.verify(AuthenticationUtil.class);
        AuthenticationUtil.authenticate(asPublicKey, FHS_OPERATOR_TOKEN_1);
        BDDMockito.verify(PropertiesHolder.class);
        PropertiesHolder.reset();
        BDDMockito.verify(FhsPublicKeysHolder.class);
        FhsPublicKeysHolder.reset();
        BDDMockito.verify(ServiceAsymmetricKeysHolder.class);
        ServiceAsymmetricKeysHolder.reset(PUBLIC_KEY_FILE_PATH, PRIVATE_KEY_FILE_PATH);
        
        Mockito.verify(this.federationHost).reload(databaseManager, communicationMechanism);
        
        assertEquals(newAuthorizationPlugin, this.applicationFacade.getAuthorizationPlugin());
    }
    
    @Test
    public void testCreateFederation() throws FogbowException {
        FederationId federationId = this.applicationFacade.createFederation(TOKEN_1, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);

        assertEquals(FEDERATION_ID_1, federationId.getId());
        assertEquals(FEDERATION_NAME_1, federationId.getName());
        assertEquals(FEDERATION_ENABLED_1, federationId.isEnabled());
        
        Mockito.verify(this.federationHost).createFederation(ADMIN_NAME_1, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testListFederations() throws FogbowException {
        List<FederationDescription> federationsDescriptions = this.applicationFacade.listFederations(TOKEN_1, ADMIN_NAME_1);
        
        assertEquals(4, federationsDescriptions.size());
        assertEquals(FEDERATION_NAME_1, federationsDescriptions.get(0).getName());
        assertEquals(FEDERATION_ID_1, federationsDescriptions.get(0).getId());
        assertEquals(FEDERATION_DESCRIPTION_1, federationsDescriptions.get(0).getDescription());
        assertEquals(FEDERATION_NAME_2, federationsDescriptions.get(1).getName());
        assertEquals(FEDERATION_ID_2, federationsDescriptions.get(1).getId());
        assertEquals(FEDERATION_DESCRIPTION_2, federationsDescriptions.get(1).getDescription());
        assertEquals(REMOTE_FEDERATION_NAME_1, federationsDescriptions.get(2).getName());
        assertEquals(REMOTE_FEDERATION_ID_1, federationsDescriptions.get(2).getId());
        assertEquals(REMOTE_FEDERATION_DESCRIPTION_1, federationsDescriptions.get(2).getDescription());
        assertEquals(REMOTE_FEDERATION_NAME_2, federationsDescriptions.get(3).getName());
        assertEquals(REMOTE_FEDERATION_ID_2, federationsDescriptions.get(3).getId());
        assertEquals(REMOTE_FEDERATION_DESCRIPTION_2, federationsDescriptions.get(3).getDescription());
        
        Mockito.verify(this.federationHost).getFederationsOwnedByUser(ADMIN_NAME_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testListFederationsNoFederation() throws FogbowException {
        List<FederationDescription> federationsDescriptions = this.applicationFacade.listFederations(TOKEN_2, ADMIN_NAME_2);
        
        assertTrue(federationsDescriptions.isEmpty());
        
        Mockito.verify(this.federationHost).getFederationsOwnedByUser(ADMIN_NAME_2);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_2));
    }
    
    @Test
    public void testGetFederationInfo() throws FogbowException {
        FederationInfo federationInfo = this.applicationFacade.getFederationInfo(TOKEN_1, FEDERATION_ID_1);
        
        assertEquals(FEDERATION_ID_1, federationInfo.getFederationId());
        assertEquals(FEDERATION_NAME_1, federationInfo.getFederationName());
        assertEquals(2, federationInfo.getnMembers().intValue());
        assertEquals(2, federationInfo.getnServices().intValue());
        
        Mockito.verify(this.federationHost).getFederation(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testDeleteFederation() throws FogbowException {
        this.applicationFacade.deleteFederation(TOKEN_1, FEDERATION_ID_1);
        
        Mockito.verify(this.federationHost).deleteFederation(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetRemoteFederationList() throws FogbowException {
        List<FederationDescription> remoteFederationList = this.applicationFacade.getRemoteFederationList(TOKEN_1);
        
        assertEquals(2, remoteFederationList.size());
        assertEquals(REMOTE_FEDERATION_ID_1, remoteFederationList.get(0).getId());
        assertEquals(REMOTE_FEDERATION_NAME_1, remoteFederationList.get(0).getName());
        assertEquals(REMOTE_FEDERATION_DESCRIPTION_1, remoteFederationList.get(0).getDescription());
        assertEquals(REMOTE_FEDERATION_ID_2, remoteFederationList.get(1).getId());
        assertEquals(REMOTE_FEDERATION_NAME_2, remoteFederationList.get(1).getName());
        assertEquals(REMOTE_FEDERATION_DESCRIPTION_2, remoteFederationList.get(1).getDescription());
        
        Mockito.verify(this.federationHost).getRemoteFederationList(ADMIN_NAME_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetEmptyRemoteFederationList() throws FogbowException {
        Mockito.when(this.federationHost.getRemoteFederationList(ADMIN_NAME_1)).thenReturn(
                Arrays.asList());
        
        List<FederationDescription> remoteFederationList = this.applicationFacade.getRemoteFederationList(TOKEN_1);
        
        assertEquals(0, remoteFederationList.size());
        Mockito.verify(this.federationHost).getRemoteFederationList(ADMIN_NAME_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testJoinRemoteFederation() throws FogbowException {
        this.applicationFacade.joinRemoteFederation(TOKEN_1, REMOTE_FEDERATION_ID_1);
        
        Mockito.verify(this.federationHost).requestToJoinRemoteFederation(ADMIN_NAME_1, REMOTE_FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetRemoteUsersAllowedAdmins() throws FogbowException {
        List<AllowedRemoteJoin> allowedAdminsJoins = this.applicationFacade.getRemoteUsersAllowedAdmins(TOKEN_1);
        
        assertEquals(3, allowedAdminsJoins.size());
        assertEquals(FEDERATION_ID_1, allowedAdminsJoins.get(0).getFederationId());
        assertEquals(FEDERATION_NAME_1, allowedAdminsJoins.get(0).getFederationName());
        assertEquals(REMOTE_FEDERATION_ADMIN_ID_1, allowedAdminsJoins.get(0).getRemoteFedAdminId());
        assertEquals(REMOTE_FHS_ID_1, allowedAdminsJoins.get(0).getFhsId());
        
        assertEquals(FEDERATION_ID_1, allowedAdminsJoins.get(1).getFederationId());
        assertEquals(FEDERATION_NAME_1, allowedAdminsJoins.get(1).getFederationName());
        assertEquals(REMOTE_FEDERATION_ADMIN_ID_2, allowedAdminsJoins.get(1).getRemoteFedAdminId());
        assertEquals(REMOTE_FHS_ID_1, allowedAdminsJoins.get(1).getFhsId());
        
        assertEquals(FEDERATION_ID_2, allowedAdminsJoins.get(2).getFederationId());
        assertEquals(FEDERATION_NAME_2, allowedAdminsJoins.get(2).getFederationName());
        assertEquals(REMOTE_FEDERATION_ADMIN_ID_1, allowedAdminsJoins.get(2).getRemoteFedAdminId());
        assertEquals(REMOTE_FHS_ID_1, allowedAdminsJoins.get(2).getFhsId());
        
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testAddRemoteUserToAllowedAdmins() throws FogbowException {
        this.applicationFacade.addRemoteUserToAllowedAdmins(TOKEN_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                REMOTE_FHS_ID_1, REMOTE_FEDERATION_ID_1);
        
        Mockito.verify(this.federationHost).addUserToAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                REMOTE_FHS_ID_1, REMOTE_FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testRemoveRemoteUserFromAllowedAdmins() throws FogbowException {
        this.applicationFacade.removeRemoteUserFromAllowedAdmins(TOKEN_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                REMOTE_FHS_ID_1, REMOTE_FEDERATION_ID_1);
        
        Mockito.verify(this.federationHost).removeUserFromAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                REMOTE_FHS_ID_1, REMOTE_FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGrantMembership() throws FogbowException {
        MemberId memberId = this.applicationFacade.grantMembership(TOKEN_1, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD, 
                USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES);
        
        assertEquals(MEMBER_ID_1, memberId.getMemberId());
        
        Mockito.verify(this.federationHost).grantMembership(ADMIN_NAME_1, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD, 
                USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testListMembers() throws FogbowException {
        List<FederationMember> members = this.applicationFacade.listMembers(TOKEN_1, FEDERATION_ID_1);
        
        assertEquals(2, members.size());
        assertEquals(MEMBER_ID_1, members.get(0).getMemberId());
        assertEquals(USER_ID_TO_ADD, members.get(0).getName());
        assertEquals(USER_DESCRIPTION_1, members.get(0).getDescription());
        assertEquals(USER_EMAIL_1, members.get(0).getEmail());
        assertEquals(USER_ENABLED_1, members.get(0).isEnabled());
        assertEquals(MEMBER_ID_2, members.get(1).getMemberId());
        assertEquals(USER_ID_2, members.get(1).getName());
        assertEquals(USER_DESCRIPTION_2, members.get(1).getDescription());
        assertEquals(USER_EMAIL_2, members.get(1).getEmail());
        assertEquals(USER_ENABLED_2, members.get(1).isEnabled());
        
        Mockito.verify(this.federationHost).getFederationMembers(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetMemberInfo() throws FogbowException {
        FederationMember federationMember = this.applicationFacade.getMemberInfo(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1);
        
        assertEquals(MEMBER_ID_1, federationMember.getMemberId());
        assertEquals(USER_ID_TO_ADD, federationMember.getName());
        assertEquals(USER_DESCRIPTION_1, federationMember.getDescription());
        assertEquals(USER_EMAIL_1, federationMember.getEmail());
        assertEquals(true, federationMember.isEnabled());
        assertEquals(2, federationMember.getAttributes().size());
        assertTrue(federationMember.getAttributes().contains(ATTRIBUTE_NAME_1));
        assertTrue(federationMember.getAttributes().contains(ATTRIBUTE_NAME_2));
    }
    
    @Test
    public void testRevokeMembership() throws FogbowException {
        this.applicationFacade.revokeMembership(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1);
        
        Mockito.verify(this.federationHost).revokeMembership(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testCreateAttribute() throws FogbowException {
        AttributeDescription returnedDescription = this.applicationFacade.createAttribute(TOKEN_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
        
        assertEquals(ATTRIBUTE_NAME_1, returnedDescription.getName());
        assertEquals(ATTRIBUTE_ID_1, returnedDescription.getId());
        
        Mockito.verify(this.federationHost).createAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetFederationAttributes() throws FogbowException {
        List<AttributeDescription> returnedDescriptions = this.applicationFacade.getFederationAttributes(TOKEN_1, FEDERATION_ID_1);
        
        assertEquals(2, returnedDescriptions.size());
        assertEquals(ATTRIBUTE_ID_1, returnedDescriptions.get(0).getId());
        assertEquals(ATTRIBUTE_NAME_1, returnedDescriptions.get(0).getName());
        assertEquals(ATTRIBUTE_ID_2, returnedDescriptions.get(1).getId());
        assertEquals(ATTRIBUTE_NAME_2, returnedDescriptions.get(1).getName());
        
        Mockito.verify(this.federationHost).getFederationAttributes(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testDeleteFederationAttribute() throws FogbowException {
        this.applicationFacade.deleteFederationAttribute(TOKEN_1, FEDERATION_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federationHost).deleteAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGrantAttribute() throws FogbowException {
        this.applicationFacade.grantAttribute(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federationHost).grantAttribute(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testRevokeAttribute() throws FogbowException {
        this.applicationFacade.revokeAttribute(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federationHost).revokeAttribute(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testRegisterService() throws FogbowException {
        ServiceId serviceId = this.applicationFacade.registerService(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME_1, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        
        assertEquals(SERVICE_ID_1, serviceId.getServiceId());
        
        Mockito.verify(this.federationHost).registerService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetServices() throws FogbowException {
        List<ServiceId> serviceIds = this.applicationFacade.getServices(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME_1);
        
        assertEquals(2, serviceIds.size());
        assertEquals(SERVICE_ID_1, serviceIds.get(0).getServiceId());
        assertEquals(SERVICE_ID_2, serviceIds.get(1).getServiceId());
        
        Mockito.verify(this.federationHost).getOwnedServices(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetService() throws FogbowException {
        ServiceInfo serviceInfo = this.applicationFacade.getService(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME_1, SERVICE_ID_1);
        
        assertEquals(SERVICE_ID_1, serviceInfo.getServiceId());
        assertEquals(SERVICE_ENDPOINT_1, serviceInfo.getEndpoint());
        assertEquals(SERVICE_METADATA_1, serviceInfo.getMetadata());
        assertEquals(SERVICE_DISCOVERY_POLICY_CLASS_NAME, serviceInfo.getDiscoveryPolicy());
        assertEquals(SERVICE_ACCESS_POLICY_CLASS_NAME, serviceInfo.getAccessPolicy());
        
        Mockito.verify(this.federationHost).getOwnedService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testUpdateService() throws FogbowException {
        this.applicationFacade.updateService(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, SERVICE_ID_1, SERVICE_METADATA_1,
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        
        Mockito.verify(this.federationHost).updateService(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1, SERVICE_ID_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testDeleteService() throws FogbowException {
        this.applicationFacade.deleteService(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, SERVICE_ID_1);
        
        Mockito.verify(this.federationHost).deleteService(ADMIN_NAME_1, FEDERATION_ID_1, MEMBER_ID_1, SERVICE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testDiscoverServices() throws FogbowException {
        List<ServiceDiscovered> discoveredService = this.applicationFacade.discoverServices(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME_1);
        
        assertEquals(2, discoveredService.size());
        assertEquals(SERVICE_ID_1, discoveredService.get(0).getServiceId());
        assertEquals(SERVICE_METADATA_1, discoveredService.get(0).getMetadata());
        assertEquals(SERVICE_ENDPOINT_1, discoveredService.get(0).getEndpoint());
        assertEquals(SERVICE_ID_2, discoveredService.get(1).getServiceId());
        assertEquals(SERVICE_METADATA_2, discoveredService.get(1).getMetadata());
        assertEquals(SERVICE_ENDPOINT_2, discoveredService.get(1).getEndpoint());
        
        Mockito.verify(this.federationHost).getAuthorizedServices(ADMIN_NAME_1, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testInvocation() throws FogbowException {
        RequestResponse response = this.applicationFacade.invocation(TOKEN_1, FEDERATION_ID_1, 
                SERVICE_ID_1, HttpMethod.GET, PATH, HEADERS, BODY);
        
        assertEquals(RESPONSE_CODE.intValue(), response.getResponseCode());
        assertEquals(RESPONSE_DATA, response.getResponseData());
        
        Mockito.verify(this.federationHost).invokeService(ADMIN_NAME_1, FEDERATION_ID_1, SERVICE_ID_1, 
                HttpMethod.GET, PATH, HEADERS, BODY);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testFhsOperatorLogin() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        String returnedToken1 = this.applicationFacade.operatorLogin(FHS_OPERATOR_ID_1, fhsOperatorCredentials1);
        String returnedToken2 = this.applicationFacade.operatorLogin(FHS_OPERATOR_ID_2, fhsOperatorCredentials2);
        
        assertEquals(FHS_OPERATOR_TOKEN_1, returnedToken1);
        assertEquals(FHS_OPERATOR_TOKEN_2, returnedToken2);
        
        Mockito.verify(this.federationHost, Mockito.never()).login(Mockito.any(), Mockito.any(), Mockito.any());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testFhsOperatorLoginOperatorNotFound() throws UnauthenticatedUserException, ConfigurationErrorException, 
    InternalServerErrorException, InvalidParameterException {
        this.applicationFacade.operatorLogin("invalidoperatorid", fhsOperatorCredentials1);
    }
    
    @Test
    public void testNotFhsOperatorLogin() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        String returnedToken = this.applicationFacade.login("", USER_ID_1, userCredentials1);
        
        assertEquals(USER_TOKEN_1, returnedToken);
        
        Mockito.verify(this.federationHost).login("", USER_ID_1, userCredentials1);
    }
    
    @Test
    public void testFederationAdminLogin() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        String returnedToken = this.applicationFacade.federationAdminLogin(ADMIN_ID_1, adminCredentials);
        
        assertEquals(ADMIN_TOKEN, returnedToken);
        
        Mockito.verify(this.federationHost).federationAdminLogin(ADMIN_ID_1, adminCredentials);
    }
    
    @Test
    public void testMap() throws FogbowException {
        Map<String, String> response = this.applicationFacade.map(TOKEN_1, FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME);
        
        assertEquals(response, CREDENTIALS);
        
        Mockito.verify(this.federationHost).map(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testIsAuthorized() throws FogbowException {
        boolean authorized = this.applicationFacade.isAuthorized(TOKEN_1, FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, this.operationToAuthorize);
        
        assertTrue(authorized);
        Mockito.verify(this.federationHost).isAuthorized(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, OPERATION_STR);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
}
