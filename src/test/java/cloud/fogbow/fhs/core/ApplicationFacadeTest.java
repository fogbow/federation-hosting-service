package cloud.fogbow.fhs.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.fhs.api.http.response.AttributeDescription;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.plugins.authentication.AuthenticationUtil;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;

// TODO add checks to authorization
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FhsPublicKeysHolder.class , AuthenticationUtil.class, 
    ServiceAsymmetricKeysHolder.class })
public class ApplicationFacadeTest {
    private static final String TOKEN_1 = "userToken";
    private static final String TOKEN_2 = "userToken2";
    private static final String ADMIN_ID = "adminId";
    private static final String ADMIN_NAME = "adminName";
    private static final String ADMIN_NAME_2 = "adminName2";
    private static final String ADMIN_EMAIL = "adminEmail";
    private static final String ADMIN_DESCRIPTION = "adminDescription";
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
    private static final Map<String, String> ADMIN_AUTHORIZATION_PROPERTIES = null;
    private static final Map<String, String> USER_AUTHORIZATION_PROPERTIES = null;
    private static final String FHS_OPERATOR_ID_1 = "fhsOperatorId1";
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
    private static final String FHS_OPERATOR_1_CREDENTIAL_KEY_1 = "fhsOperator1CredentialKey1";
    private static final String FHS_OPERATOR_1_CREDENTIAL_KEY_2 = "fhsOperator1CredentialKey2";
    private static final String FHS_OPERATOR_2_CREDENTIAL_KEY_1 = "fhsOperator2CredentialKey1";
    private static final String FHS_OPERATOR_2_CREDENTIAL_KEY_2 = "fhsOperator2CredentialKey2";
    private static final String FHS_OPERATOR_1_CREDENTIAL_VALUE_1 = "fhsOperator1CredentialValue1";
    private static final String FHS_OPERATOR_1_CREDENTIAL_VALUE_2 = "fhsOperator1CredentialValue2";
    private static final String FHS_OPERATOR_2_CREDENTIAL_VALUE_1 = "fhsOperator2CredentialValue1";
    private static final String FHS_OPERATOR_2_CREDENTIAL_VALUE_2 = "fhsOperator2CredentialValue2";
    
    private ApplicationFacade applicationFacade;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    private RSAPublicKey asPublicKey;
    private Federation federation1;
    private Federation federation2;
    private SystemUser systemUser1;
    private SystemUser systemUser2;
    private FederationUser federationUser1;
    private FederationUser federationUser2;
    private FederationUser fhsOperator1;
    private FederationUser fhsOperator2;
    private List<FederationUser> fhsOperators;
    private Map<String, String> fhsOperatorCredentials1;
    private Map<String, String> fhsOperatorCredentials2;
    private Map<String, String> userCredentials1;
    private FederationService federationService;
    private FederationService federationService2; 
    private ServiceDiscoveryPolicy serviceDiscoveryPolicy;
    private ServiceInvoker serviceInvoker;
    private FederationAttribute federationAttribute1;
    private FederationAttribute federationAttribute2;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationAuthenticationPlugin authenticationPlugin;
    
    @Before
    public void setUp() throws FogbowException {
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
        Mockito.when(federationUser1.isEnabled()).thenReturn(USER_ENABLED_1);
        
        federationUser2 = Mockito.mock(FederationUser.class);
        Mockito.when(federationUser2.getMemberId()).thenReturn(MEMBER_ID_2);
        Mockito.when(federationUser2.getName()).thenReturn(USER_ID_2);
        Mockito.when(federationUser2.getDescription()).thenReturn(USER_DESCRIPTION_2);
        Mockito.when(federationUser2.getEmail()).thenReturn(USER_EMAIL_2);
        Mockito.when(federationUser2.isEnabled()).thenReturn(USER_ENABLED_2);
        
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
        
        federation1 = Mockito.mock(Federation.class);
        Mockito.when(federation1.getName()).thenReturn(FEDERATION_NAME_1);
        Mockito.when(federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(federation1.getDescription()).thenReturn(FEDERATION_DESCRIPTION_1);
        Mockito.when(federation1.enabled()).thenReturn(FEDERATION_ENABLED_1);
        
        federation2 = Mockito.mock(Federation.class);
        Mockito.when(federation2.getName()).thenReturn(FEDERATION_NAME_2);
        Mockito.when(federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(federation2.getDescription()).thenReturn(FEDERATION_DESCRIPTION_2);
        Mockito.when(federation2.enabled()).thenReturn(FEDERATION_ENABLED_2);
        
        federationAttribute1 = Mockito.mock(FederationAttribute.class);
        Mockito.when(federationAttribute1.getId()).thenReturn(ATTRIBUTE_ID_1);
        Mockito.when(federationAttribute1.getName()).thenReturn(ATTRIBUTE_NAME_1);
        
        federationAttribute2 = Mockito.mock(FederationAttribute.class);
        Mockito.when(federationAttribute2.getId()).thenReturn(ATTRIBUTE_ID_2);
        Mockito.when(federationAttribute2.getName()).thenReturn(ATTRIBUTE_NAME_2);
        
        asPublicKey = Mockito.mock(RSAPublicKey.class);
        
        systemUser1 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser1.getId()).thenReturn(ADMIN_NAME);
        systemUser2 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser2.getId()).thenReturn(ADMIN_NAME_2);
        
        ServiceAsymmetricKeysHolder keysHolder = Mockito.mock(ServiceAsymmetricKeysHolder.class);
        Mockito.when(keysHolder.getPublicKey()).thenReturn(asPublicKey);
        
        PowerMockito.mockStatic(ServiceAsymmetricKeysHolder.class);
        BDDMockito.given(ServiceAsymmetricKeysHolder.getInstance()).willReturn(keysHolder);
        
        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1)).willReturn(systemUser1);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_2)).willReturn(systemUser2);
        
        this.fhsOperatorCredentials1 = new HashMap<String, String>();
        this.fhsOperatorCredentials1.put(FHS_OPERATOR_1_CREDENTIAL_KEY_1, FHS_OPERATOR_1_CREDENTIAL_VALUE_1);
        this.fhsOperatorCredentials1.put(FHS_OPERATOR_1_CREDENTIAL_KEY_2, FHS_OPERATOR_1_CREDENTIAL_VALUE_2);
        
        this.fhsOperatorCredentials2 = new HashMap<String, String>();
        this.fhsOperatorCredentials2.put(FHS_OPERATOR_2_CREDENTIAL_KEY_1, FHS_OPERATOR_2_CREDENTIAL_VALUE_1);
        this.fhsOperatorCredentials2.put(FHS_OPERATOR_2_CREDENTIAL_KEY_2, FHS_OPERATOR_2_CREDENTIAL_VALUE_2);
        
        this.userCredentials1 = new HashMap<String, String>();
        
        this.authenticationPlugin = Mockito.mock(FederationAuthenticationPlugin.class);
        Mockito.when(this.authenticationPlugin.authenticate(fhsOperatorCredentials1)).thenReturn(FHS_OPERATOR_TOKEN_1);
        Mockito.when(this.authenticationPlugin.authenticate(fhsOperatorCredentials2)).thenReturn(FHS_OPERATOR_TOKEN_2);
        Mockito.when(this.authenticationPlugin.authenticate(userCredentials1)).thenReturn(USER_TOKEN_1);
        
        this.authenticationPluginInstantiator = Mockito.mock(FederationAuthenticationPluginInstantiator.class);
        Mockito.when(this.authenticationPluginInstantiator.getAuthenticationPlugin(
                AUTHENTICATION_PLUGIN_CLASS_NAME, AUTHENTICATION_PLUGIN_PROPERTIES)).thenReturn(authenticationPlugin);
        
        this.authorizationPlugin = Mockito.mock(AuthorizationPlugin.class);
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.addFederationAdmin(ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES)).thenReturn(ADMIN_ID);
        Mockito.when(this.federationHost.createFederation(ADMIN_NAME, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1)).thenReturn(federation1);
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME)).
                thenReturn(Arrays.asList(federation1, federation2));
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_2)).thenReturn(Arrays.asList());
        Mockito.when(this.federationHost.grantMembership(ADMIN_NAME, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD,
                USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES)).thenReturn(federationUser1);
        Mockito.when(this.federationHost.getFederationMembers(ADMIN_NAME, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationUser1, federationUser2));
        Mockito.when(this.federationHost.createAttribute(ADMIN_NAME, FEDERATION_ID_1, ATTRIBUTE_NAME_1)).thenReturn(ATTRIBUTE_ID_1);
        Mockito.when(this.federationHost.getFederationAttributes(ADMIN_NAME, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationAttribute1, federationAttribute2));
        Mockito.when(this.federationHost.registerService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME)).thenReturn(SERVICE_ID_1);
        Mockito.when(this.federationHost.getOwnedServices(ADMIN_NAME, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(SERVICE_ID_1, SERVICE_ID_2));
        Mockito.when(this.federationHost.getOwnedService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ID_1)).
                thenReturn(federationService);
        Mockito.when(this.federationHost.getAuthorizedServices(ADMIN_NAME, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationService, federationService2));
        Mockito.when(this.federationHost.invokeService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ID_1, 
                HttpMethod.GET, PATH, HEADERS, BODY)).thenReturn(new DefaultServiceResponse(RESPONSE_CODE, RESPONSE_DATA));
        Mockito.when(this.federationHost.map(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME)).thenReturn(CREDENTIALS);
        Mockito.when(this.federationHost.login("", USER_ID_1, userCredentials1)).thenReturn(USER_TOKEN_1);
        
        this.fhsOperators = new ArrayList<FederationUser>();
        this.fhsOperators.add(fhsOperator1);
        this.fhsOperators.add(fhsOperator2);
        
        applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
        applicationFacade.setFhsOperators(fhsOperators);
        applicationFacade.setAuthenticationPluginInstantiator(authenticationPluginInstantiator);
    }
    
    @Test
    public void testAddFederationAdmin() throws FogbowException {
        String returnedAdminId = this.applicationFacade.addFederationAdmin(TOKEN_1, ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES);
        
        assertEquals(ADMIN_ID, returnedAdminId);
        
        Mockito.verify(this.federationHost).addFederationAdmin(ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED, ADMIN_AUTHORIZATION_PROPERTIES);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testCreateFederation() throws FogbowException {
        FederationId federationId = this.applicationFacade.createFederation(TOKEN_1, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);

        assertEquals(FEDERATION_ID_1, federationId.getId());
        assertEquals(FEDERATION_NAME_1, federationId.getName());
        assertEquals(FEDERATION_ENABLED_1, federationId.isEnabled());
        
        Mockito.verify(this.federationHost).createFederation(ADMIN_NAME, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testListFederations() throws FogbowException {
        List<FederationDescription> federationsDescriptions = this.applicationFacade.listFederations(TOKEN_1, ADMIN_NAME);
        
        assertEquals(2, federationsDescriptions.size());
        assertEquals(FEDERATION_NAME_1, federationsDescriptions.get(0).getName());
        assertEquals(FEDERATION_ID_1, federationsDescriptions.get(0).getId());
        assertEquals(FEDERATION_DESCRIPTION_1, federationsDescriptions.get(0).getDescription());
        assertEquals(FEDERATION_NAME_2, federationsDescriptions.get(1).getName());
        assertEquals(FEDERATION_ID_2, federationsDescriptions.get(1).getId());
        assertEquals(FEDERATION_DESCRIPTION_2, federationsDescriptions.get(1).getDescription());
        
        Mockito.verify(this.federationHost).getFederationsOwnedByUser(ADMIN_NAME);
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
    public void testGrantMembership() throws FogbowException {
        MemberId memberId = this.applicationFacade.grantMembership(TOKEN_1, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD, USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES);
        
        assertEquals(MEMBER_ID_1, memberId.getMemberId());
        
        Mockito.verify(this.federationHost).grantMembership(ADMIN_NAME, FEDERATION_ID_1, USER_ID_TO_ADD, USER_EMAIL_TO_ADD, USER_DESCRIPTION_TO_ADD, USER_AUTHORIZATION_PROPERTIES);
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
        
        Mockito.verify(this.federationHost).getFederationMembers(ADMIN_NAME, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testCreateAttribute() throws FogbowException {
        AttributeDescription returnedDescription = this.applicationFacade.createAttribute(TOKEN_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
        
        assertEquals(ATTRIBUTE_NAME_1, returnedDescription.getName());
        assertEquals(ATTRIBUTE_ID_1, returnedDescription.getId());
        
        Mockito.verify(this.federationHost).createAttribute(ADMIN_NAME, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
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
        
        Mockito.verify(this.federationHost).getFederationAttributes(ADMIN_NAME, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGrantAttribute() throws FogbowException {
        this.applicationFacade.grantAttribute(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federationHost).grantAttribute(ADMIN_NAME, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testRevokeAttribute() throws FogbowException {
        this.applicationFacade.revokeAttribute(TOKEN_1, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federationHost).revokeAttribute(ADMIN_NAME, FEDERATION_ID_1, MEMBER_ID_1, ATTRIBUTE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testRegisterService() throws FogbowException {
        ServiceId serviceId = this.applicationFacade.registerService(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        
        assertEquals(SERVICE_ID_1, serviceId.getServiceId());
        
        Mockito.verify(this.federationHost).registerService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetServices() throws FogbowException {
        List<ServiceId> serviceIds = this.applicationFacade.getServices(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME);
        
        assertEquals(2, serviceIds.size());
        assertEquals(SERVICE_ID_1, serviceIds.get(0).getServiceId());
        assertEquals(SERVICE_ID_2, serviceIds.get(1).getServiceId());
        
        Mockito.verify(this.federationHost).getOwnedServices(ADMIN_NAME, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetService() throws FogbowException {
        ServiceInfo serviceInfo = this.applicationFacade.getService(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ID_1);
        
        assertEquals(SERVICE_ID_1, serviceInfo.getServiceId());
        assertEquals(SERVICE_ENDPOINT_1, serviceInfo.getEndpoint());
        assertEquals(SERVICE_METADATA_1, serviceInfo.getMetadata());
        assertEquals(SERVICE_DISCOVERY_POLICY_CLASS_NAME, serviceInfo.getDiscoveryPolicy());
        assertEquals(SERVICE_ACCESS_POLICY_CLASS_NAME, serviceInfo.getAccessPolicy());
        
        Mockito.verify(this.federationHost).getOwnedService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testDiscoverServices() throws FogbowException {
        List<ServiceDiscovered> discoveredService = this.applicationFacade.discoverServices(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME);
        
        assertEquals(2, discoveredService.size());
        assertEquals(SERVICE_ID_1, discoveredService.get(0).getServiceId());
        assertEquals(SERVICE_METADATA_1, discoveredService.get(0).getMetadata());
        assertEquals(SERVICE_ENDPOINT_1, discoveredService.get(0).getEndpoint());
        assertEquals(SERVICE_ID_2, discoveredService.get(1).getServiceId());
        assertEquals(SERVICE_METADATA_2, discoveredService.get(1).getMetadata());
        assertEquals(SERVICE_ENDPOINT_2, discoveredService.get(1).getEndpoint());
        
        Mockito.verify(this.federationHost).getAuthorizedServices(ADMIN_NAME, FEDERATION_ID_1);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testInvocation() throws FogbowException {
        RequestResponse response = this.applicationFacade.invocation(TOKEN_1, FEDERATION_ID_1, 
                SERVICE_ID_1, HttpMethod.GET, PATH, HEADERS, BODY);
        
        assertEquals(RESPONSE_CODE.intValue(), response.getResponseCode());
        assertEquals(RESPONSE_DATA, response.getResponseData());
        
        Mockito.verify(this.federationHost).invokeService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ID_1, 
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
    
    @Test
    public void testNotFhsOperatorLogin() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        String returnedToken = this.applicationFacade.login("", USER_ID_1, userCredentials1);
        
        assertEquals(USER_TOKEN_1, returnedToken);
        
        Mockito.verify(this.federationHost).login("", USER_ID_1, userCredentials1);
    }
    
    @Test
    public void testMap() throws FogbowException {
        Map<String, String> response = this.applicationFacade.map(TOKEN_1, FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME);
        
        assertEquals(response, CREDENTIALS);
        
        Mockito.verify(this.federationHost).map(FEDERATION_ID_1, SERVICE_ID_1, USER_ID_1, CLOUD_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
}
