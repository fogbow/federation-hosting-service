package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdate;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FederationAttribute.class })
public class FederationTest {
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String FHS_ID_3 = "fhsId3";
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription";
    private static final boolean FEDERATION_ENABLED = true;
    private static final String USER_ID_TO_ADD = "userIdToAdd";
    private static final String USER_EMAIL_TO_ADD = "userEmailToAdd";
    private static final String USER_DESCRIPTION_TO_ADD = "userDescriptionToAdd";
    private static final String FEDERATION_USER_ID_1 = "userFederationId1";
    private static final String FEDERATION_USER_NAME_1 = "userId1";
    private static final String FEDERATION_USER_EMAIL_1 = "userEmail1";
    private static final String FEDERATION_USER_DESCRIPTION_1 = "userDescription1";
    private static final boolean FEDERATION_USER_ENABLED_1 = true;
    private static final String FEDERATION_USER_ID_2 = "userFederationId2";
    private static final String FEDERATION_USER_NAME_2 = "userId2";
    private static final String FEDERATION_USER_EMAIL_2 = "userEmail2";
    private static final String FEDERATION_USER_DESCRIPTION_2 = "userDescription2";
    private static final boolean FEDERATION_USER_ENABLED_2 = true;
    private static final String FEDERATION_USER_ID_3 = "federationUserId3";
    private static final String FEDERATION_USER_NAME_3 = "federationUserName3";
    private static final String FEDERATION_USER_EMAIL_3 = "federationUserEmail3";
    private static final String FEDERATION_USER_DESCRIPTION_3 = "federationUserDescription3";
    private static final boolean FEDERATION_USER_ENABLED_3 = true;
    private static final String NOT_REGISTERED_USER_ID = "notRegisteredUserId";
    private static final String REMOTE_FED_ADMIN_ID_1 = "remoteFedAdminId1";
    private static final String REMOTE_FED_ADMIN_ID_2 = "remoteFedAdminId2";
    private static final String REMOTE_ADMIN_ID_1 = "remoteAdminId1";
    private static final String REMOTE_ADMIN_USER_NAME_1 = "remoteAdminUserName1";
    private static final String REMOTE_ADMIN_EMAIL_1 = "remoteAdminEmail1";
    private static final String REMOTE_ADMIN_DESCRIPTION_1 = "remoteAdminDescription1";
    private static final boolean REMOTE_ADMIN_ENABLED_1 = true;
    private static final String REMOTE_ADMIN_ID_2 = "remoteAdminId1";
    private static final String REMOTE_ADMIN_USER_NAME_2 = "remoteAdminUserName1";
    private static final String REMOTE_ADMIN_EMAIL_2 = "remoteAdminEmail2";
    private static final String REMOTE_ADMIN_DESCRIPTION_2 = "remoteAdminDescription2";
    private static final boolean REMOTE_ADMIN_ENABLED_2 = true;
    private static final String REMOTE_USER_ID_1 = "remoteUserId1";
    private static final String REMOTE_USER_NAME_1 = "remoteUserName1";
    private static final String REMOTE_USER_EMAIL_1 = "remoteUserEmail1";
    private static final String REMOTE_USER_DESCRIPTION_1 = "remoteUserDescription1";
    private static final boolean REMOTE_USER_ENABLED_1 = true;
    private static final String FEDERATION_SERVICE_ID_1 = "federationServiceId1";
    private static final String FEDERATION_SERVICE_ID_2 = "federationServiceId2";
    private static final String FEDERATION_SERVICE_ID_3 = "federationServiceId3";
    private static final String ATTRIBUTE_ID_1 = "attributeId1";
    private static final String ATTRIBUTE_NAME_1 = "attributeName1";
    private static final String ATTRIBUTE_ID_2 = "attributeId2";
    private static final String ATTRIBUTE_NAME_2 = "attributeName2";
    private static final String ATTRIBUTE_ID_3 = "attributeId3";
    private static final String ATTRIBUTE_NAME_3 = "attributeName3";
    private static final String IDENTITY_PLUGIN_CLASS_NAME = "identityPluginClassName";
    private static final String FEDERATION_USER_TOKEN_1 = "userToken1";
    private static final String FEDERATION_USER_TOKEN_2 = "userToken2";
    private static final String FEDERATION_USER_1_CREDENTIAL_KEY = "federationUser1CredentialKey";
    private static final String FEDERATION_USER_1_CREDENTIAL_VALUE = "federationUser1CredentialValue";
    private static final String FEDERATION_USER_2_CREDENTIAL_KEY = "federationUser2CredentialKey";
    private static final String FEDERATION_USER_2_CREDENTIAL_VALUE = "federationUser2CredentialValue";
    private static final String CLOUD_NAME = "cloudName";
    private static final String CREDENTIALS_KEY = "credentialsKey";
    private static final String CREDENTIALS_VALUE = "credentialsValue";
    private static final String SERVICE_OWNER_ID = "serviceOwnerId";
    private static final String SERVICE_DISCOVERY_POLICY_CLASS_NAME = "discoveryPolicyClassName";
    private static final String SERVICE_ACCESS_POLICY_CLASS_NAME = "accessPolicyClassName";
    private static final String SERVICE_ENDPOINT = "serviceEndpoint";
    private static final String FEDERATION_MEMBERS_STR = "federationMembers";
    private static final String FEDERATION_SERVICES_STR = "federationServices";
    private static final String FEDERATION_ATTRIBUTES_STR = "federationAttributes";
    private static final String ALLOWED_ADMINS_STR = "allowedAdmins";
    private static final String REMOTE_ADMINS_STR = "remoteAdmins";
    private static final String FEDERATION_METADATA_STR = "metadata";
    private static final String PATH_COMPONENT_1 = "pathComponent1";
    private static final String PATH_COMPONENT_2 = "pathComponent2";
    private static final String HEADER_KEY_1 = "headerKey1";
    private static final String HEADER_KEY_2 = "headerKey2";
    private static final String HEADER_VALUE_1 = "headerValue1";
    private static final String HEADER_VALUE_2 = "headerValue2";
    private static final String BODY_KEY_1 = "bodyKey1";
    private static final String BODY_KEY_2 = "bodyKey2";
    private static final Object BODY_VALUE_1 = "bodyValue1";
    private static final Object BODY_VALUE_2 = "bodyValue2";
    private static final String UPDATE_NEW_NAME = "updatedFederationName1";
    private static final String UPDATE_NEW_DESCRIPTION = "updatedNewDescription1";
    private static final Boolean UPDATED_NEW_ENABLED = !FEDERATION_ENABLED;
    private static final String UPDATED_FEDERATION_USER_DESCRIPTION_1 = "updatedFederationUserDescription1";
    private static final String FEDERATION_SERVICE_STR = "federationServiceStr";
    private static final String FEDERATION_SERVICE_STR_3 = "federationServiceStr3";
    private static final String UPDATED_ATTRIBUTE_NAME_1 = "updatedAttributeName1";
    private static final String FEDERATION_METADATA_KEY_1 = "federationMetadataKey1";
    private static final String FEDERATION_METADATA_KEY_2 = "federationMetadataKey2";
    private static final String FEDERATION_METADATA_VALUE_1 = "federationMetadataValue1";
    private static final String FEDERATION_METADATA_VALUE_2 = "federationMetadataValue2";
    private static final String UPDATED_FEDERATION_METADATA_VALUE_1 = "updatedFederationMetadataValue1";
    private static final String UPDATED_FEDERATION_USER_1_STR = "updatedFederationUser1";
    private static final String FEDERATION_USER_3_STR = "federationUser3";
    private static final String UPDATED_FEDERATION_ATTRIBUTE_1_STR = "updatedFederationAttribute1";
    private static final String FEDERATION_ATTRIBUTE_3_STR = "federationAttribute3";

    private Federation federation;
    private List<FederationUser> federationMembers;
    private List<FederationUser> remoteAdmins;
    private List<RemoteFederationUser> allowedAdmins;
    private FederationUser federationUser1;
    private FederationUser updatedFederationUser1;
    private FederationUser federationUser2;
    private FederationUser federationUser3;
    private FederationUser remoteAdmin1;
    private FederationUser remoteAdmin2;
    private FederationUser remoteUser1;
    private List<FederationService> federationServices;
    private FederationService federationService1;
    private FederationService updatedFederationService1;
    private FederationService federationService2;
    private FederationService federationService3;
    private Map<String, String> serviceMetadata;
    private ServiceAccessPolicy accessPolicy;
    private ServiceAccessPolicy accessPolicyFederationService3;
    private List<FederationAttribute> federationAttributes;
    private FederationAttribute federationAttribute1;
    private FederationAttribute updatedFederationAttribute1;
    private FederationAttribute federationAttribute2;
    private FederationAttribute federationAttribute3;
    private Map<String, String> federationUserCredentials1;
    private Map<String, String> federationUserCredentials2;
    private Map<String, String> credentials;
    private Map<String, String> federationMetadata1;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationAuthenticationPlugin authenticationPlugin;
    private FederationServiceFactory federationServiceFactory;
    private JsonUtils jsonUtils;
    
    @Before
    public void setUp() throws Exception {
        this.federationMetadata1 = new HashMap<String, String>();
        this.federationMetadata1.put(FEDERATION_METADATA_KEY_1, FEDERATION_METADATA_VALUE_1);
        this.federationMetadata1.put(FEDERATION_METADATA_KEY_2, FEDERATION_METADATA_VALUE_2);
        
        serviceMetadata = new HashMap<String, String>();
        
        this.federationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        this.federationUser2 = new FederationUser(FEDERATION_USER_ID_2, FEDERATION_USER_NAME_2, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_2, FEDERATION_USER_DESCRIPTION_2, FEDERATION_USER_ENABLED_2, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        this.federationUser3 = new FederationUser(FEDERATION_USER_ID_3, FEDERATION_USER_NAME_3, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_3, FEDERATION_USER_DESCRIPTION_3, FEDERATION_USER_ENABLED_3, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        this.remoteAdmin1 = new FederationUser(REMOTE_ADMIN_ID_1, REMOTE_ADMIN_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_2, REMOTE_ADMIN_EMAIL_1, REMOTE_ADMIN_DESCRIPTION_1, REMOTE_ADMIN_ENABLED_1, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, true);
        this.remoteAdmin2 = new FederationUser(REMOTE_ADMIN_ID_2, REMOTE_ADMIN_USER_NAME_2, FEDERATION_ID_1,
                FHS_ID_3, REMOTE_ADMIN_EMAIL_2, REMOTE_ADMIN_DESCRIPTION_2, REMOTE_ADMIN_ENABLED_2, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, true);
        this.remoteUser1 = new FederationUser(REMOTE_USER_ID_1, REMOTE_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_2, REMOTE_USER_EMAIL_1, REMOTE_USER_DESCRIPTION_1, REMOTE_USER_ENABLED_1, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        this.updatedFederationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_1, UPDATED_FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        this.federationMembers.add(this.remoteUser1);
        
        this.allowedAdmins = new ArrayList<RemoteFederationUser>();
        this.remoteAdmins = new ArrayList<FederationUser>();
        
        this.credentials = new HashMap<String, String>();
        this.credentials.put(CREDENTIALS_KEY, CREDENTIALS_VALUE);
        
        this.accessPolicy = Mockito.mock(ServiceAccessPolicy.class);
        Mockito.when(this.accessPolicy.getCredentialsForAccess(federationUser1, CLOUD_NAME)).thenReturn(credentials);
        Mockito.when(this.accessPolicy.isAllowedToPerform(
                Mockito.eq(federationUser1), Mockito.any(ServiceOperation.class))).thenReturn(true);
        this.accessPolicyFederationService3 = Mockito.mock(ServiceAccessPolicy.class);
        
        this.federationService1 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService1.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_1);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser1)).thenReturn(true);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser2)).thenReturn(false);
        Mockito.when(this.federationService1.getAccessPolicy()).thenReturn(accessPolicy);
        this.federationService2 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService2.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_2);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser1)).thenReturn(false);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser2)).thenReturn(true);
        this.updatedFederationService1 = Mockito.mock(FederationService.class);
        Mockito.when(this.updatedFederationService1.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_1);
        Mockito.when(this.updatedFederationService1.isDiscoverableBy(federationUser1)).thenReturn(false);
        Mockito.when(this.updatedFederationService1.isDiscoverableBy(federationUser2)).thenReturn(true);
        Mockito.when(this.updatedFederationService1.getAccessPolicy()).thenReturn(accessPolicy);
        this.federationService3 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService3.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_3);
        Mockito.when(this.federationService3.isDiscoverableBy(federationUser1)).thenReturn(true);
        Mockito.when(this.federationService3.isDiscoverableBy(federationUser2)).thenReturn(true);
        Mockito.when(this.federationService3.getAccessPolicy()).thenReturn(accessPolicyFederationService3);
        
        this.federationServices = new ArrayList<FederationService>();
        this.federationServices.add(federationService1);
        this.federationServices.add(federationService2);
        
        this.federationAttribute1 = new FederationAttribute(ATTRIBUTE_ID_1, ATTRIBUTE_NAME_1);
        this.federationAttribute2 = new FederationAttribute(ATTRIBUTE_ID_2, ATTRIBUTE_NAME_2);
        this.updatedFederationAttribute1 = new FederationAttribute(ATTRIBUTE_ID_1, UPDATED_ATTRIBUTE_NAME_1);
        this.federationAttribute3 = new FederationAttribute(ATTRIBUTE_ID_3, ATTRIBUTE_NAME_3);
        
        this.federationAttributes = new ArrayList<FederationAttribute>();
        this.federationAttributes.add(federationAttribute1);
        this.federationAttributes.add(federationAttribute2);
        
        this.federationUserCredentials1 = new HashMap<String, String>();
        this.federationUserCredentials1.put(FEDERATION_USER_1_CREDENTIAL_KEY, FEDERATION_USER_1_CREDENTIAL_VALUE);
        this.federationUserCredentials2 = new HashMap<String, String>();
        this.federationUserCredentials2.put(FEDERATION_USER_2_CREDENTIAL_KEY, FEDERATION_USER_2_CREDENTIAL_VALUE);
        
        this.authenticationPlugin = Mockito.mock(FederationAuthenticationPlugin.class);
        Mockito.when(this.authenticationPlugin.authenticate(federationUserCredentials1)).thenReturn(FEDERATION_USER_TOKEN_1);
        Mockito.when(this.authenticationPlugin.authenticate(federationUserCredentials2)).thenReturn(FEDERATION_USER_TOKEN_2);
        
        this.authenticationPluginInstantiator = Mockito.mock(FederationAuthenticationPluginInstantiator.class);
        Mockito.when(this.authenticationPluginInstantiator.getAuthenticationPlugin(IDENTITY_PLUGIN_CLASS_NAME, 
                new HashMap<String, String>())).thenReturn(authenticationPlugin);
        
        this.federationServiceFactory = Mockito.mock(FederationServiceFactory.class);
        Mockito.when(this.federationServiceFactory.createService(SERVICE_OWNER_ID, SERVICE_ENDPOINT, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME, FEDERATION_ID_1, 
                serviceMetadata)).thenReturn(federationService1);
        Mockito.when(this.federationServiceFactory.deserialize(FEDERATION_SERVICE_STR_3)).thenReturn(federationService3);
        Mockito.when(this.federationServiceFactory.deserialize(FEDERATION_SERVICE_STR)).thenReturn(updatedFederationService1);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.fromJson(UPDATED_FEDERATION_USER_1_STR, FederationUser.class)).thenReturn(updatedFederationUser1);
        Mockito.when(this.jsonUtils.fromJson(FEDERATION_USER_3_STR, FederationUser.class)).thenReturn(federationUser3);
        
        PowerMockito.mockStatic(FederationAttribute.class);
        BDDMockito.given(FederationAttribute.deserialize(FEDERATION_ATTRIBUTE_3_STR)).willReturn(federationAttribute3);
        BDDMockito.given(FederationAttribute.deserialize(UPDATED_FEDERATION_ATTRIBUTE_1_STR)).willReturn(updatedFederationAttribute1);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
    }
    
    @Test
    public void testAddUser() throws InvalidParameterException {
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationServices = new ArrayList<FederationService>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, 
                this.federationServices, this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        List<FederationUser> federationUserListBefore = this.federation.getMemberList();
        
        assertEquals(0, federationUserListBefore.size());
        
        this.federation.addUser(USER_ID_TO_ADD, USER_EMAIL_TO_ADD, USER_DESCRIPTION_TO_ADD, new HashMap<String, String>());
        
        List<FederationUser> federationUserListAfter = this.federation.getMemberList();
        
        assertEquals(1, federationUserListAfter.size());
        FederationUser user = federationUserListAfter.get(0);
        assertEquals(USER_ID_TO_ADD, user.getName());
    }
    
    @Test
    public void testRevokeMembership() throws InvalidParameterException {
        assertEquals(3, this.federationMembers.size());
        
        this.federation.revokeMembership(FEDERATION_USER_ID_1);
        
        assertEquals(2, this.federationMembers.size());
        assertTrue(this.federationMembers.contains(federationUser2));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRevokeMembershipOfNonMember() throws InvalidParameterException {
        this.federation.revokeMembership("unknownmemberid");
    }
    
    @Test
    public void testGetUser() throws InvalidParameterException {
        FederationUser returnedFederationUser1 = this.federation.getUserByMemberId(FEDERATION_USER_ID_1);
        FederationUser returnedFederationUser2 = this.federation.getUserByMemberId(FEDERATION_USER_ID_2);
        
        assertEquals(this.federationUser1, returnedFederationUser1);
        assertEquals(this.federationUser2, returnedFederationUser2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetUserUserDoesNotBelongToFederation() throws InvalidParameterException {
        this.federation.getUserByMemberId(NOT_REGISTERED_USER_ID);
    }
    
    @Test
    public void testIsFederationOwner() {
        this.remoteAdmins = new ArrayList<FederationUser>();
        this.remoteAdmins.add(this.remoteAdmin1);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        assertTrue(this.federation.isFederationOwner(FEDERATION_OWNER_1));
        assertTrue(this.federation.isFederationOwner(REMOTE_ADMIN_USER_NAME_1));
        assertFalse(this.federation.isFederationOwner(REMOTE_USER_NAME_1));
    }
    
    @Test
    public void testIsRemoteAdmin() {
        this.remoteAdmins = new ArrayList<FederationUser>();
        this.remoteAdmins.add(this.remoteAdmin1);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        assertTrue(this.federation.isRemoteAdmin(REMOTE_ADMIN_USER_NAME_1));
        assertFalse(this.federation.isRemoteAdmin(FEDERATION_OWNER_1));
        assertFalse(this.federation.isRemoteAdmin(FEDERATION_USER_NAME_1));
        assertFalse(this.federation.isRemoteAdmin(FEDERATION_USER_NAME_2));
        assertFalse(this.federation.isRemoteAdmin(REMOTE_USER_NAME_1));
    }
    
    @Test
    public void testRegisterAndGetServices() throws InvalidParameterException {
        this.federationServices = new ArrayList<FederationService>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator,
                this.federationServiceFactory, this.jsonUtils);
        
        List<FederationService> servicesBeforeRegister = this.federation.getServices();
        assertEquals(0, servicesBeforeRegister.size());

        this.federation.registerService(SERVICE_OWNER_ID, SERVICE_ENDPOINT, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME, serviceMetadata);
        
        List<FederationService> servicesAfterRegister = this.federation.getServices();
        assertEquals(1, servicesAfterRegister.size());
        assertEquals(this.federationService1, servicesAfterRegister.get(0));
    }
    
    @Test
    public void testGetService() throws InvalidParameterException {
        FederationService returnedService1 = this.federation.getService(FEDERATION_SERVICE_ID_1);
        FederationService returnedService2 = this.federation.getService(FEDERATION_SERVICE_ID_2);
        
        assertEquals(this.federationService1, returnedService1);
        assertEquals(this.federationService2, returnedService2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetNotRegisteredService() throws InvalidParameterException {
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        this.federation.getService("unregisteredServiceId");
    }
    
    @Test
    public void testDeleteService() throws InvalidParameterException {
        assertEquals(2, this.federationServices.size());
        
        this.federation.deleteService(FEDERATION_SERVICE_ID_1);
        
        assertEquals(1, this.federationServices.size());
        assertTrue(this.federationServices.contains(federationService2));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteNotRegisteredService() throws InvalidParameterException {
        this.federation.deleteService("unknownserviceid");
    }
    
    @Test
    public void testGetAuthorizedServices() throws InvalidParameterException {
        List<FederationService> authorizedServicesUser1 = this.federation.getAuthorizedServices(FEDERATION_USER_NAME_1);
        List<FederationService> authorizedServicesUser2 = this.federation.getAuthorizedServices(FEDERATION_USER_NAME_2);
        
        assertEquals(1, authorizedServicesUser1.size());
        assertEquals(this.federationService1, authorizedServicesUser1.get(0));
        
        assertEquals(1, authorizedServicesUser2.size());
        assertEquals(this.federationService2, authorizedServicesUser2.get(0));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetAuthorizedServicesNotRegisteredUser() throws InvalidParameterException {
        this.federation.getAuthorizedServices("unregisteredUserId");
    }
    
    @Test
    public void testCreateAndGetAttributes() {
        this.federationAttributes = new ArrayList<FederationAttribute>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        List<FederationAttribute> attributesBeforeCreation = this.federation.getAttributes();
        
        assertEquals(2, attributesBeforeCreation.size());
        assertEquals(Federation.MEMBER_ATTRIBUTE_NAME, attributesBeforeCreation.get(0).getName());
        assertEquals(Federation.MEMBER_ATTRIBUTE_NAME, attributesBeforeCreation.get(0).getId());
        assertEquals(Federation.SERVICE_OWNER_ATTRIBUTE_NAME, attributesBeforeCreation.get(1).getName());
        assertEquals(Federation.SERVICE_OWNER_ATTRIBUTE_NAME, attributesBeforeCreation.get(1).getId());
        
        String returnedAttributeId = this.federation.createAttribute(ATTRIBUTE_NAME_1);
        
        assertNotNull(returnedAttributeId);
        
        List<FederationAttribute> attributesAfterCreation = this.federation.getAttributes();
        
        assertEquals(3, attributesAfterCreation.size());
        assertEquals(Federation.MEMBER_ATTRIBUTE_NAME, attributesBeforeCreation.get(0).getName());
        assertEquals(Federation.MEMBER_ATTRIBUTE_NAME, attributesBeforeCreation.get(0).getId());
        assertEquals(Federation.SERVICE_OWNER_ATTRIBUTE_NAME, attributesBeforeCreation.get(1).getName());
        assertEquals(Federation.SERVICE_OWNER_ATTRIBUTE_NAME, attributesBeforeCreation.get(1).getId());
        assertEquals(ATTRIBUTE_NAME_1, attributesAfterCreation.get(2).getName());
        assertEquals(returnedAttributeId, attributesAfterCreation.get(2).getId());
    }
    
    @Test
    public void testDeleteAttribute() throws InvalidParameterException {
        assertEquals(2, this.federationAttributes.size());
        
        this.federation.deleteAttribute(ATTRIBUTE_ID_1);
        
        assertEquals(1, this.federationAttributes.size());
        assertTrue(this.federationAttributes.contains(this.federationAttribute2));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteServiceOwnerAttribute() throws InvalidParameterException {
        this.federation.deleteAttribute(Federation.SERVICE_OWNER_ATTRIBUTE_NAME);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteMemberAttribute() throws InvalidParameterException {
        this.federation.deleteAttribute(Federation.MEMBER_ATTRIBUTE_NAME);
    }
    
    @Test
    public void testGrantAttribute() throws InvalidParameterException {
        List<String> attributesBeforeGrant = this.federationUser1.getAttributes();
        
        assertTrue(attributesBeforeGrant.isEmpty());
        
        this.federation.grantAttribute(FEDERATION_USER_ID_1, ATTRIBUTE_ID_1);
        
        List<String> attributesAfterGrant = this.federationUser1.getAttributes();
        
        assertEquals(1, attributesAfterGrant.size());
        assertEquals(ATTRIBUTE_ID_1, attributesAfterGrant.get(0));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGrantAttributeFailsIfAttributeDoesNotExist() throws InvalidParameterException {
        this.federation.grantAttribute(FEDERATION_USER_ID_1, "invalidattributeid");
    }
    
    @Test
    public void testRevokeAttribute() throws InvalidParameterException {
        List<String> federationUser1Attributes = new ArrayList<String>();
        federationUser1Attributes.add(ATTRIBUTE_ID_1);
        
        this.federationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                federationUser1Attributes, IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        List<String> attributesBeforeRevoke = this.federationUser1.getAttributes();
        
        assertEquals(1, attributesBeforeRevoke.size());
        assertEquals(ATTRIBUTE_ID_1, attributesBeforeRevoke.get(0));
        
        this.federation.revokeAttribute(FEDERATION_USER_ID_1, ATTRIBUTE_ID_1);
        
        List<String> attributesAfterRevoke = this.federationUser1.getAttributes();
        assertTrue(attributesAfterRevoke.isEmpty());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testRevokeAttributeFailsIfAttributeDoesNotExist() throws InvalidParameterException {
        this.federation.revokeAttribute(FEDERATION_USER_ID_1, "invalidattributeid");
    }
    
    @Test
    public void testIsServiceOwner() throws InvalidParameterException {
        List<String> federationUser1Attributes = new ArrayList<String>();
        federationUser1Attributes.add(Federation.SERVICE_OWNER_ATTRIBUTE_NAME);
        
        this.federationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, FEDERATION_ID_1,
                FHS_ID_1, FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                federationUser1Attributes, IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), 
                false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        assertTrue(this.federation.isServiceOwner(FEDERATION_USER_NAME_1));
        assertFalse(this.federation.isServiceOwner(FEDERATION_USER_NAME_2));
    }
    
    @Test
    public void testLogin() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        String returnedToken1 = this.federation.login(FEDERATION_USER_ID_1, this.federationUserCredentials1);
        String returnedToken2 = this.federation.login(FEDERATION_USER_ID_2, this.federationUserCredentials2);
        
        assertEquals(FEDERATION_USER_TOKEN_1, returnedToken1);
        assertEquals(FEDERATION_USER_TOKEN_2, returnedToken2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testLoginUserNotFound() throws InvalidParameterException, UnauthenticatedUserException, 
    ConfigurationErrorException, InternalServerErrorException {
        this.federation.login("notfederateduser", this.federationUserCredentials1);
    }
    
    @Test
    public void testMap() throws InvalidParameterException {
        Map<String, String> returnedCredentials = this.federation.map(FEDERATION_SERVICE_ID_1, 
                FEDERATION_USER_NAME_1, CLOUD_NAME);
        
        assertEquals(this.credentials, returnedCredentials);
        
        Mockito.verify(this.accessPolicy).getCredentialsForAccess(federationUser1, CLOUD_NAME);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotMapCredentialsForInvalidService() throws InvalidParameterException {
        this.federation.map("invalidserviceid", FEDERATION_USER_NAME_1, CLOUD_NAME);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotMapCredentialsForInvalidMember() throws InvalidParameterException {
        this.federation.map(FEDERATION_SERVICE_ID_1, "invaliduserid", CLOUD_NAME);
    }
    
    @Test
    public void testInvoke() throws FogbowException {
        List<String> path = Arrays.asList(PATH_COMPONENT_1, PATH_COMPONENT_2);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_1, HEADER_VALUE_1);
        headers.put(HEADER_KEY_2, HEADER_VALUE_2);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(BODY_KEY_1, BODY_VALUE_1);
        body.put(BODY_KEY_2, BODY_VALUE_2);
        
        this.federation.invoke(FEDERATION_USER_NAME_1, FEDERATION_SERVICE_ID_1, HttpMethod.GET, path, headers, body);
        
        Mockito.verify(this.federationService1).invoke(federationUser1, HttpMethod.GET, path, headers, body);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testInvokeUserIsNotAuthorized() throws FogbowException {
        List<String> path = Arrays.asList(PATH_COMPONENT_1, PATH_COMPONENT_2);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_1, HEADER_VALUE_1);
        headers.put(HEADER_KEY_2, HEADER_VALUE_2);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(BODY_KEY_1, BODY_VALUE_1);
        body.put(BODY_KEY_2, BODY_VALUE_2);
        
        Mockito.when(this.accessPolicy.isAllowedToPerform(
                Mockito.eq(federationUser1), Mockito.any(ServiceOperation.class))).thenReturn(false);
        
        this.federation.invoke(FEDERATION_USER_NAME_1, FEDERATION_SERVICE_ID_1, HttpMethod.GET, path, headers, body);
    }
    
    @Test
    public void testAddRemoteUserAsAllowedFedAdmin() throws InvalidParameterException {
        assertTrue(this.federation.getAllowedRemoteJoins().isEmpty());
        
        this.federation.addRemoteUserAsAllowedFedAdmin(REMOTE_FED_ADMIN_ID_1, FHS_ID_2);
        
        assertEquals(1, this.federation.getAllowedRemoteJoins().size());
        assertEquals(REMOTE_FED_ADMIN_ID_1, this.federation.getAllowedRemoteJoins().get(0).getFedAdminId());
        assertEquals(FHS_ID_2, this.federation.getAllowedRemoteJoins().get(0).getFhsId());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAlreadyRegisteredAllowedFedAdmin() throws InvalidParameterException {
        this.allowedAdmins = new ArrayList<RemoteFederationUser>();
        this.allowedAdmins.add(new RemoteFederationUser(REMOTE_FED_ADMIN_ID_1, FHS_ID_2));
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);

        this.federation.addRemoteUserAsAllowedFedAdmin(REMOTE_FED_ADMIN_ID_1, FHS_ID_2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAllowedFedAdminWithNullId() throws InvalidParameterException {
        this.federation.addRemoteUserAsAllowedFedAdmin(null, FHS_ID_2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAllowedFedAdminWithEmptyId() throws InvalidParameterException {
        this.federation.addRemoteUserAsAllowedFedAdmin("", FHS_ID_2);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAllowedFedAdminWithNullFhsId() throws InvalidParameterException {
        this.federation.addRemoteUserAsAllowedFedAdmin(REMOTE_FED_ADMIN_ID_1, null);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAllowedFedAdminWithEmptyFhsId() throws InvalidParameterException {
        this.federation.addRemoteUserAsAllowedFedAdmin(REMOTE_FED_ADMIN_ID_1, "");
    }
    
    @Test
    public void testRemoveRemoteUserAsAllowedFedAdmin() throws InvalidParameterException {
        this.allowedAdmins = new ArrayList<RemoteFederationUser>();
        this.allowedAdmins.add(new RemoteFederationUser(REMOTE_FED_ADMIN_ID_1, FHS_ID_2));
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        this.federation.removeRemoteUserFromAllowedAdmins(REMOTE_FED_ADMIN_ID_1, FHS_ID_2);
        
        assertTrue(this.federation.getAllowedRemoteJoins().isEmpty());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRemoveNotRegisteredAllowedFedAdmin() throws InvalidParameterException {
        this.federation.removeRemoteUserFromAllowedAdmins(REMOTE_FED_ADMIN_ID_1, FHS_ID_2);
    }
    
    @Test
    public void testAddRemoteAdmin() throws InvalidParameterException {
        this.allowedAdmins = new ArrayList<RemoteFederationUser>();
        this.allowedAdmins.add(new RemoteFederationUser(REMOTE_FED_ADMIN_ID_1, FHS_ID_2));
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        FederationUser remoteFedAdmin1 = Mockito.mock(FederationUser.class);
        Mockito.when(remoteFedAdmin1.getName()).thenReturn(REMOTE_FED_ADMIN_ID_1);
        
        this.federation.addRemoteAdmin(remoteFedAdmin1, FHS_ID_2);
        
        assertEquals(1, this.federation.getRemoteAdmins().size());
        assertEquals(remoteFedAdmin1, this.federation.getRemoteAdmins().get(0));
    }
    
    @Test
    public void testCannotAddNotAuthorizedRemoteAdmin() {
        this.allowedAdmins = new ArrayList<RemoteFederationUser>();
        this.allowedAdmins.add(new RemoteFederationUser(REMOTE_FED_ADMIN_ID_1, FHS_ID_2));
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        FederationUser remoteFedAdmin2 = Mockito.mock(FederationUser.class);
        Mockito.when(remoteFedAdmin2.getName()).thenReturn(REMOTE_FED_ADMIN_ID_2);
        
        try {
            this.federation.addRemoteAdmin(remoteFedAdmin2, FHS_ID_2);
            Assert.fail("Expected InvalidParameterException.");
        } catch (InvalidParameterException e) {
            
        }
        
        assertTrue(this.federation.getRemoteAdmins().isEmpty());
    }
    
    @Test
    public void testGetSupportingFhssNoRemoteAdmin() {
        List<String> supportingFhss = this.federation.getSupportingFhss();
        assertTrue(supportingFhss.isEmpty());
    }
    
    @Test
    public void testGetSupportingFhss() {
        this.remoteAdmins.add(remoteAdmin1);
        this.remoteAdmins.add(remoteAdmin2);
        
        List<String> supportingFhss = this.federation.getSupportingFhss();
        
        assertEquals(2, supportingFhss.size());
        assertTrue(supportingFhss.contains(FHS_ID_2));
        assertTrue(supportingFhss.contains(FHS_ID_3));
    }
    
    @Test
    public void testUpdate() throws InvalidParameterException {
        List<String> newMembers = new ArrayList<String>();
        newMembers.add(UPDATED_FEDERATION_USER_1_STR);
        newMembers.add(FEDERATION_USER_3_STR);
        
        List<String> newServices = new ArrayList<String>();
        newServices.add(FEDERATION_SERVICE_STR);
        newServices.add(FEDERATION_SERVICE_STR_3);
        
        List<String> newAttributes = new ArrayList<String>();
        newAttributes.add(UPDATED_FEDERATION_ATTRIBUTE_1_STR);
        newAttributes.add(FEDERATION_ATTRIBUTE_3_STR);
        
        List<String> membersToDelete = new ArrayList<String>();
        membersToDelete.add(FEDERATION_USER_NAME_2);
        
        List<String> servicesToDelete = new ArrayList<String>();
        servicesToDelete.add(FEDERATION_SERVICE_ID_2);
        
        List<String> attributesToDelete = new ArrayList<String>();
        attributesToDelete.add(ATTRIBUTE_ID_2);
        
        Map<String, String> updateMetadata = new HashMap<String, String>();
        updateMetadata.put(FEDERATION_METADATA_KEY_1, UPDATED_FEDERATION_METADATA_VALUE_1);
        updateMetadata.put(FEDERATION_METADATA_KEY_2, FEDERATION_METADATA_VALUE_2);
        
        FederationUpdate remoteUpdate = new FederationUpdate(false, FEDERATION_ID_1,
                UPDATE_NEW_NAME, UPDATE_NEW_DESCRIPTION, UPDATED_NEW_ENABLED,
                newMembers, newServices, newAttributes, membersToDelete, servicesToDelete,
                attributesToDelete, updateMetadata);
        this.federation.update(remoteUpdate);
        
        assertEquals(UPDATE_NEW_NAME, this.federation.getName());
        assertEquals(UPDATE_NEW_DESCRIPTION, this.federation.getDescription());
        assertEquals(UPDATED_NEW_ENABLED, this.federation.enabled());
        
        assertEquals(3, this.federation.getMemberList().size());
        assertTrue(this.federation.getMemberList().contains(updatedFederationUser1));
        assertTrue(this.federation.getMemberList().contains(this.remoteUser1));
        assertTrue(this.federation.getMemberList().contains(this.federationUser3));
        assertFalse(this.federation.getMemberList().contains(this.federationUser1));
        assertFalse(this.federation.getMemberList().contains(this.federationUser2));
        
        assertEquals(2, this.federation.getServices().size());
        assertTrue(this.federation.getServices().contains(this.updatedFederationService1));
        assertTrue(this.federation.getServices().contains(this.federationService3));
        assertFalse(this.federation.getServices().contains(this.federationService1));
        assertFalse(this.federation.getServices().contains(this.federationService2));
        
        assertEquals(4, this.federation.getAttributes().size());
        assertTrue(this.federation.getAttributes().contains(this.updatedFederationAttribute1));
        assertTrue(this.federation.getAttributes().contains(this.federationAttribute3));
        assertFalse(this.federation.getAttributes().contains(this.federationAttribute1));
        assertFalse(this.federation.getAttributes().contains(this.federationAttribute2));
        
        assertEquals(2, this.federation.getMetadata().size());
        assertEquals(UPDATED_FEDERATION_METADATA_VALUE_1, this.federation.getMetadata().get(FEDERATION_METADATA_KEY_1));
        assertEquals(FEDERATION_METADATA_VALUE_2, this.federation.getMetadata().get(FEDERATION_METADATA_KEY_2));
    }
    
    @Test
    public void testToJson() {
        this.allowedAdmins.add(Mockito.mock(RemoteFederationUser.class));
        this.remoteAdmins.add(Mockito.mock(FederationUser.class));
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(this.jsonUtils.toJson(FEDERATION_ENABLED)).thenReturn("true");
        Mockito.when(this.jsonUtils.toJson(this.federationMembers)).thenReturn(FEDERATION_MEMBERS_STR);
        Mockito.when(this.jsonUtils.toJson(this.federationServices)).thenReturn(FEDERATION_SERVICES_STR);
        Mockito.when(this.jsonUtils.toJson(this.federationAttributes)).thenReturn(FEDERATION_ATTRIBUTES_STR);
        Mockito.when(this.jsonUtils.toJson(this.allowedAdmins)).thenReturn(ALLOWED_ADMINS_STR);
        Mockito.when(this.jsonUtils.toJson(this.remoteAdmins)).thenReturn(REMOTE_ADMINS_STR);
        Mockito.when(this.jsonUtils.toJson(federationMetadata1)).thenReturn(FEDERATION_METADATA_STR);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FHS_ID_1, federationMetadata1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.allowedAdmins, this.remoteAdmins, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory, this.jsonUtils);
        
        String serializedFederation = this.federation.toJson();
        String expectedSerializedFederation = 
                FEDERATION_ID_1 + Federation.SERIALIZATION_SEPARATOR +
                FEDERATION_OWNER_1 + Federation.SERIALIZATION_SEPARATOR +
                FEDERATION_NAME_1 + Federation.SERIALIZATION_SEPARATOR +
                FHS_ID_1 + Federation.SERIALIZATION_SEPARATOR +
                FEDERATION_DESCRIPTION_1 + Federation.SERIALIZATION_SEPARATOR +
                "true" + Federation.SERIALIZATION_SEPARATOR + 
                FEDERATION_MEMBERS_STR + Federation.SERIALIZATION_SEPARATOR +
                FEDERATION_SERVICES_STR + Federation.SERIALIZATION_SEPARATOR +
                FEDERATION_ATTRIBUTES_STR + Federation.SERIALIZATION_SEPARATOR + 
                ALLOWED_ADMINS_STR + Federation.SERIALIZATION_SEPARATOR +
                REMOTE_ADMINS_STR + Federation.SERIALIZATION_SEPARATOR + 
                FEDERATION_METADATA_STR;
        
        assertEquals(expectedSerializedFederation, serializedFederation);
    }
}
