package cloud.fogbow.fhs.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.core.datastore.DatabaseManager;
import cloud.fogbow.fhs.core.intercomponent.FederationUpdateBuilder;
import cloud.fogbow.fhs.core.intercomponent.FhsCommunicationMechanism;
import cloud.fogbow.fhs.core.intercomponent.SynchronizationMechanism;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationFactory;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;
import cloud.fogbow.fhs.core.models.ServiceOperation;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.utils.JsonUtils;
import cloud.fogbow.fhs.core.utils.TestUtils;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class FederationHostTest {
    private static final String ADMIN_ID_1 = "adminId1";
    private static final String ADMIN_ID_2 = "adminId2";
    private static final String ADMIN_NAME_1 = "admin1";
    private static final String ADMIN_NAME_2 = "admin2";
    private static final String ADMIN_EMAIL_1 = "adminEmail1";
    private static final String ADMIN_EMAIL_2 = "adminEmail2";
    private static final String ADMIN_DESCRIPTION_1 = "adminDescription1";
    private static final String ADMIN_DESCRIPTION_2 = "adminDescription2";
    private static final boolean ADMIN_ENABLED_1 = true;
    private static final boolean ADMIN_ENABLED_2 = true;
    private static final String UPDATED_ADMIN_NAME_1 = "updatedAdminName1";
    private static final String UPDATED_ADMIN_EMAIL_1 = "updatedAdminEmail1";
    private static final String UPDATED_ADMIN_DESCRIPTION_1 = "updatedAdminDescription1";
    private static final boolean UPDATED_ADMIN_ENABLED_1 = false;
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_ID_3 = "federationId3";
    private static final String FEDERATION_NAME_1 = "federation1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final boolean FEDERATION_ENABLED_1 = true;
    private static final boolean UPDATED_FEDERATION_ENABLED_1 = false;
    private static final String USER_ID_TO_GRANT_MEMBERSHIP = "userIdToGrantMembership";
    private static final String USER_EMAIL_TO_GRANT_MEMBERSHIP = "userEmailToGrantMembership";
    private static final String USER_DESCRIPTION_TO_GRANT_MEMBERSHIP = "userDescriptionToGrantMembership";
    private static final String REGULAR_USER_ID_1 = "regularUserId1";
    private static final String REGULAR_USER_ID_2 = "regularUserId2";
    private static final String REGULAR_USER_NAME_1 = "regularUser1";
    private static final String REGULAR_USER_NAME_2 = "regularUser2";
    private static final String REGULAR_USER_EMAIL_1 = "regularUserEmail1";
    private static final String REGULAR_USER_EMAIL_2 = "regularUserEmail2";
    private static final String REGULAR_USER_DESCRIPTION_1 = "regularUserDescription1";
    private static final String REGULAR_USER_DESCRIPTION_2 = "regularUserDescription2";
    private static final boolean REGULAR_USER_ENABLED_1 = true;
    private static final boolean REGULAR_USER_ENABLED_2 = true;
    private static final String SERVICE_ID_1 = "serviceId1";
    private static final String SERVICE_ID_2 = "serviceId2";
    private static final String SERVICE_ID_3 = "serviceId3";
    private static final String SERVICE_ENDPOINT_1 = "endpoint1";
    private static final String SERVICE_ENDPOINT_2 = "endpoint2";
    private static final String SERVICE_ENDPOINT_3 = "endpoint3";
    private static final String SERVICE_DISCOVERY_POLICY_CLASS_NAME_1 = "serviceDiscoveryPolicyClassName1";
    private static final String SERVICE_INVOKER_CLASS_NAME_1 = "serviceInvokerClassName1";
    private static final String ACCESS_POLICY_CLASS_NAME = "accessPolicyClassName";
    private static final Map<String, String> SERVICE_METADATA_1 = new HashMap<String, String>();
    private static final Map<String, String> SERVICE_METADATA_2 = new HashMap<String, String>();
    private static final Map<String, String> SERVICE_METADATA_3 = new HashMap<String, String>();
    private static final String CLOUD_NAME = "cloudName";
    private static final String CREDENTIALS_STRING = "credentialsString";
    private static final String CREDENTIAL_KEY_1 = "credentialKey1";
    private static final String CREDENTIAL_VALUE_1 = "credentialValue1";
    private static final String CREDENTIAL_KEY_2 = "credentialKey2";
    private static final String CREDENTIAL_VALUE_2 = "credentialValue2";
    private static final String SERVICE_OWNER_NAME_1 = "serviceOwner1";
    private static final String SERVICE_OWNER_NAME_2 = "serviceOwner2";
    private static final String ATTRIBUTE_NAME_1 = "attributeName1";
    private static final String ATTRIBUTE_ID_1 = "attributeId1";
    private static final Map<String, String> USER_AUTHORIZATION_PROPERTIES = new HashMap<String, String>();
    private static final String IDENTITY_PLUGIN_CLASS_NAME = "identityPluginClassName";
    private static final String ADMIN_TOKEN_1 = "adminToken1";
    private static final String REGULAR_USER_TOKEN_1 = "regularUserToken1";
    private static final String ADMIN_CREDENTIAL_KEY_1 = "adminCredentialKey1";
    private static final String ADMIN_CREDENTIAL_KEY_2 = "adminCredentialKey2";
    private static final String ADMIN_CREDENTIAL_VALUE_1 = "adminCredentialValue1";
    private static final String ADMIN_CREDENTIAL_VALUE_2 = "adminCredentialValue2";
    private static final String REGULAR_USER_CREDENTIAL_KEY_1 = "regularUserCredentialKey1";
    private static final String REGULAR_USER_CREDENTIAL_KEY_2 = "regularUserCredentialKey2";
    private static final String REGULAR_USER_CREDENTIAL_VALUE_1 = "regularUserCredentialValue1";
    private static final String REGULAR_USER_CREDENTIAL_VALUE_2 = "regularUserCredentialValue2";
    private static final String FHS_ID_1 = "fhsId1";
    private static final String FHS_ID_2 = "fhsId2";
    private static final String PROVIDER_ID = "providerId";
    private static final String REMOTE_FEDERATION_ID_1 = "remoteFederationId1";
    private static final String REMOTE_FEDERATION_NAME_1 = "remoteFederationName1";
    private static final String UPDATED_REMOTE_FEDERATION_NAME_1 = "updatedRemoteFederationName1";
    private static final String REMOTE_FEDERATION_DESCRIPTION_1 = "remoteFederationDescription1";
    private static final boolean REMOTE_FEDERATION_ENABLED_1 = true;
    private static final String REMOTE_FEDERATION_FHS_ID_1 = FHS_ID_2;
    private static final String REMOTE_FEDERATION_OWNING_ADMIN_ID_1 = "remoteFederationOwningAdminId1";
    private static final String REMOTE_FEDERATION_ID_2 = "remoteFederationId2";
    private static final String REMOTE_FEDERATION_NAME_2 = "remoteFederationName2";
    private static final String REMOTE_FEDERATION_DESCRIPTION_2 = "remoteFederationDescription2";
    private static final String UPDATED_REMOTE_FEDERATION_DESCRIPTION_2 = "updatedRemoteFederationDescription2";
    private static final boolean REMOTE_FEDERATION_ENABLED_2 = true;
    private static final String REMOTE_FEDERATION_FHS_ID_2 = FHS_ID_2;
    private static final String REMOTE_FEDERATION_OWNING_ADMIN_ID_2 = "remoteFederationOwningAdminId2";
    private static final String REMOTE_FEDERATION_ADMIN_ID_1 = "remoteFederationAdminId1";
    
    private FederationHost federationHost;
    private FederationUser admin1;
    private FederationUser admin2;
    private FederationUser user1;
    private FederationUser user2;
    private Federation federation1;
    private Federation federation2;
    private Federation federation3;
    private List<FederationUser> adminList;
    private List<Federation> federationList;
    private Map<String, String> federationMetadata;
    private Map<String, Map<String, String>> credentialsMap;
    private Map<String, String> credentialsMapCloud1;
    private FederationService service1;
    private FederationService service2;
    private FederationService service3;
    private ServiceInvoker invoker;
    private ServiceDiscoveryPolicy discoveryPolicy1;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationAuthenticationPlugin authenticationPlugin;
    private JsonUtils jsonUtils;
    private ServiceAccessPolicy accessPolicy;
    private FederationAttribute federationAttribute1;
    private FederationAttribute federationAttribute2;
    private Map<String, String> adminCredentials1;
    private Map<String, String> regularUserCredentials1;
    private Map<String, String> updatedServiceMetadata;
    private FederationFactory federationFactory;
    private DatabaseManager databaseManager;
    private PropertiesHolder propertiesHolder;
    private FhsCommunicationMechanism communicationMechanism;
    private List<RemoteFederation> remoteFederations;
    private Federation remoteFederation1;
    private SynchronizationMechanism synchronizationMechanism;
    private FederationUpdateBuilder updateBuilder;
    
    private void setUpFederationData() throws FogbowException {
        this.databaseManager = Mockito.mock(DatabaseManager.class);
        Mockito.when(this.databaseManager.getFederationAdmins()).thenReturn(new ArrayList<FederationUser>());
        Mockito.when(this.databaseManager.getFederations()).thenReturn(new ArrayList<Federation>());
        
        this.updatedServiceMetadata = new HashMap<String, String>();
        this.updatedServiceMetadata.put(FederationHost.INVOKER_CLASS_NAME_METADATA_KEY, SERVICE_INVOKER_CLASS_NAME_1);
        
        this.invoker = Mockito.mock(ServiceInvoker.class);
        
        this.admin1 = new FederationUser(ADMIN_ID_1, ADMIN_NAME_1, FEDERATION_ID_1, FHS_ID_1, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, 
                ADMIN_ENABLED_1, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, USER_AUTHORIZATION_PROPERTIES, false, true);
        this.admin2 = new FederationUser(ADMIN_ID_2, ADMIN_NAME_2, FEDERATION_ID_1, FHS_ID_1, ADMIN_EMAIL_2, ADMIN_DESCRIPTION_2, 
                ADMIN_ENABLED_2, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, USER_AUTHORIZATION_PROPERTIES, false, true);
        this.user1 = new FederationUser(REGULAR_USER_ID_1, REGULAR_USER_NAME_1, FEDERATION_ID_1, FHS_ID_1, REGULAR_USER_EMAIL_1, 
                REGULAR_USER_DESCRIPTION_1, REGULAR_USER_ENABLED_1, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, 
                USER_AUTHORIZATION_PROPERTIES, false, false);
        this.user2 = new FederationUser(REGULAR_USER_ID_2, REGULAR_USER_NAME_2, FEDERATION_ID_1, FHS_ID_1, REGULAR_USER_EMAIL_2, 
                REGULAR_USER_DESCRIPTION_2, REGULAR_USER_ENABLED_2, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, 
                USER_AUTHORIZATION_PROPERTIES, false, false);

        this.discoveryPolicy1 = Mockito.mock(ServiceDiscoveryPolicy.class);
        Mockito.when(discoveryPolicy1.isDiscoverableBy(user1)).thenReturn(true);
        Mockito.when(discoveryPolicy1.isDiscoverableBy(user2)).thenReturn(false);
        
        this.accessPolicy = Mockito.mock(ServiceAccessPolicy.class);
        Mockito.when(this.accessPolicy.isAllowedToPerform(user1, new ServiceOperation(HttpMethod.GET))).thenReturn(true);
        Mockito.when(this.accessPolicy.isAllowedToPerform(user2, new ServiceOperation(HttpMethod.GET))).thenReturn(false);
        
        this.adminCredentials1 = new HashMap<String, String>();
        this.adminCredentials1.put(ADMIN_CREDENTIAL_KEY_1, ADMIN_CREDENTIAL_VALUE_1);
        this.adminCredentials1.put(ADMIN_CREDENTIAL_KEY_2, ADMIN_CREDENTIAL_VALUE_2);
        
        this.regularUserCredentials1 = new HashMap<String, String>();
        this.regularUserCredentials1.put(REGULAR_USER_CREDENTIAL_KEY_1, REGULAR_USER_CREDENTIAL_VALUE_1);
        this.regularUserCredentials1.put(REGULAR_USER_CREDENTIAL_KEY_2, REGULAR_USER_CREDENTIAL_VALUE_2);
        
        this.authenticationPlugin = Mockito.mock(FederationAuthenticationPlugin.class);
        Mockito.when(this.authenticationPlugin.authenticate(adminCredentials1)).thenReturn(ADMIN_TOKEN_1);
        Mockito.when(this.authenticationPlugin.authenticate(regularUserCredentials1)).thenReturn(REGULAR_USER_TOKEN_1);
        
        this.authenticationPluginInstantiator = Mockito.mock(FederationAuthenticationPluginInstantiator.class);
        Mockito.when(this.authenticationPluginInstantiator.getAuthenticationPlugin(
                IDENTITY_PLUGIN_CLASS_NAME, USER_AUTHORIZATION_PROPERTIES)).thenReturn(authenticationPlugin);
        
        this.federationMetadata = new HashMap<String, String>();
        this.federationMetadata.put(FederationHost.CREDENTIALS_METADATA_KEY, 
                CREDENTIALS_STRING);
        this.credentialsMapCloud1 = new HashMap<String, String>();
        this.credentialsMapCloud1.put(CREDENTIAL_KEY_1, CREDENTIAL_VALUE_1);
        this.credentialsMapCloud1.put(CREDENTIAL_KEY_2, CREDENTIAL_VALUE_2);
        this.credentialsMap = new HashMap<String, Map<String, String>>();
        this.credentialsMap.put(CLOUD_NAME, this.credentialsMapCloud1);
        
        this.federationAttribute1 = Mockito.mock(FederationAttribute.class);
        this.federationAttribute2 = Mockito.mock(FederationAttribute.class);
        
        this.service1 = Mockito.mock(FederationService.class);
        Mockito.when(this.service1.getServiceId()).thenReturn(SERVICE_ID_1);
        Mockito.when(this.service1.getOwnerId()).thenReturn(SERVICE_OWNER_NAME_1);
        Mockito.when(this.service1.getEndpoint()).thenReturn(SERVICE_ENDPOINT_1);
        Mockito.when(this.service1.getDiscoveryPolicy()).thenReturn(discoveryPolicy1);
        Mockito.when(this.service1.getInvoker()).thenReturn(invoker);
        Mockito.when(this.service1.getAccessPolicy()).thenReturn(accessPolicy);
        Mockito.when(this.service1.getMetadata()).thenReturn(SERVICE_METADATA_1);
        
        this.service2 = Mockito.mock(FederationService.class);
        Mockito.when(this.service2.getServiceId()).thenReturn(SERVICE_ID_2);
        Mockito.when(this.service2.getOwnerId()).thenReturn(SERVICE_OWNER_NAME_2);
        Mockito.when(this.service2.getEndpoint()).thenReturn(SERVICE_ENDPOINT_2);
        Mockito.when(this.service2.getDiscoveryPolicy()).thenReturn(discoveryPolicy1);
        Mockito.when(this.service2.getInvoker()).thenReturn(invoker);
        Mockito.when(this.service2.getAccessPolicy()).thenReturn(accessPolicy);
        Mockito.when(this.service2.getMetadata()).thenReturn(SERVICE_METADATA_2);
        
        this.service3 = Mockito.mock(FederationService.class);
        Mockito.when(this.service3.getServiceId()).thenReturn(SERVICE_ID_3);
        Mockito.when(this.service3.getOwnerId()).thenReturn(SERVICE_OWNER_NAME_1);
        Mockito.when(this.service3.getEndpoint()).thenReturn(SERVICE_ENDPOINT_3);
        Mockito.when(this.service3.getDiscoveryPolicy()).thenReturn(discoveryPolicy1);
        Mockito.when(this.service3.getInvoker()).thenReturn(invoker);
        Mockito.when(this.service3.getAccessPolicy()).thenReturn(accessPolicy);
        Mockito.when(this.service3.getMetadata()).thenReturn(SERVICE_METADATA_3);
        
        List<FederationService> authorizedServices = Arrays.asList(this.service1, this.service2);
        
        this.federation1 = Mockito.mock(Federation.class);
        Mockito.when(federation1.getId()).thenReturn(FEDERATION_ID_1, FEDERATION_ID_1);
        Mockito.when(federation1.getUserById(REGULAR_USER_NAME_1)).thenReturn(user1);
        Mockito.when(federation1.getUserByMemberId(REGULAR_USER_ID_1)).thenReturn(user1);
        Mockito.when(federation1.getOwner()).thenReturn(ADMIN_NAME_1);
        Mockito.when(federation1.isFederationOwner(ADMIN_NAME_1)).thenReturn(true);
        Mockito.when(federation1.isFederationOwner(ADMIN_NAME_2)).thenReturn(false);
        Mockito.when(federation1.getService(SERVICE_ID_1)).thenReturn(service1);
        Mockito.when(federation1.getService(SERVICE_ID_2)).thenReturn(service2);
        Mockito.when(federation1.getService(SERVICE_ID_3)).thenReturn(service3);
        Mockito.when(federation1.getServices()).thenReturn(Arrays.asList(service1, service2, service3));
        Mockito.when(federation1.getAuthorizedServices(REGULAR_USER_NAME_1)).thenReturn(authorizedServices);
        Mockito.when(federation1.getMemberList()).thenReturn(Arrays.asList(user1, user2));
        Mockito.when(federation1.getMetadata()).thenReturn(federationMetadata);
        Mockito.when(federation1.isServiceOwner(SERVICE_OWNER_NAME_1)).thenReturn(true);
        Mockito.when(federation1.isServiceOwner(SERVICE_OWNER_NAME_2)).thenReturn(true);
        Mockito.when(federation1.createAttribute(ATTRIBUTE_NAME_1)).thenReturn(ATTRIBUTE_ID_1);
        Mockito.when(federation1.getAttributes()).thenReturn(
                Arrays.asList(this.federationAttribute1, this.federationAttribute2));
        Mockito.when(federation1.login(REGULAR_USER_ID_1, this.regularUserCredentials1)).thenReturn(
                REGULAR_USER_TOKEN_1);
        Mockito.when(federation1.registerService(SERVICE_OWNER_NAME_1, SERVICE_ENDPOINT_1,
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1, SERVICE_METADATA_1)).thenReturn(SERVICE_ID_1);
        Mockito.when(federation1.map(SERVICE_ID_1, REGULAR_USER_ID_1, CLOUD_NAME)).thenReturn(
                credentialsMapCloud1);
        
        this.federation2 = Mockito.mock(Federation.class);
        Mockito.when(federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(federation2.getOwner()).thenReturn(ADMIN_NAME_2);
        
        this.federation3 = Mockito.mock(Federation.class);
        Mockito.when(federation3.getId()).thenReturn(FEDERATION_ID_3);
        Mockito.when(federation3.getOwner()).thenReturn(ADMIN_NAME_1);
        
        this.adminList = TestUtils.getMockedList(2, admin1, admin2);
        this.federationList = TestUtils.getMockedList(2, federation1, federation2, federation3);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(jsonUtils.fromJson(CREDENTIALS_STRING, Map.class)).thenReturn(credentialsMap);
        
        this.federationFactory = Mockito.mock(FederationFactory.class);
        Mockito.when(this.federationFactory.createFederationFactory(ADMIN_NAME_1, FEDERATION_NAME_1, 
                federationMetadata, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1)).thenReturn(federation1);
        
        this.remoteFederation1 = Mockito.mock(Federation.class);
        
        this.communicationMechanism = Mockito.mock(FhsCommunicationMechanism.class);
        Mockito.when(this.communicationMechanism.joinRemoteFederation(admin1, REMOTE_FEDERATION_ID_1, FHS_ID_2)).thenReturn(remoteFederation1);
        
        this.updateBuilder = Mockito.mock(FederationUpdateBuilder.class);
        
        Mockito.when(this.updateBuilder.updateFederation(Mockito.anyString())).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.withMember(Mockito.any(FederationUser.class))).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.withService(Mockito.anyString())).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.withAttribute(Mockito.any(FederationAttribute.class))).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.deleteMember(Mockito.anyString())).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.deleteService(Mockito.anyString())).thenReturn(updateBuilder);
        Mockito.when(this.updateBuilder.deleteAttribute(Mockito.anyString())).thenReturn(updateBuilder);
        
        this.federationHost = new FederationHost(adminList, federationList, jsonUtils, authenticationPluginInstantiator, 
                this.federationFactory, databaseManager, communicationMechanism, this.updateBuilder);
        
        this.remoteFederations = new ArrayList<RemoteFederation>();
        this.remoteFederations.add(new RemoteFederation(REMOTE_FEDERATION_ID_1, REMOTE_FEDERATION_NAME_1, 
                REMOTE_FEDERATION_DESCRIPTION_1, REMOTE_FEDERATION_ENABLED_1, 
                REMOTE_FEDERATION_OWNING_ADMIN_ID_1, REMOTE_FEDERATION_FHS_ID_1));
        this.remoteFederations.add(new RemoteFederation(REMOTE_FEDERATION_ID_2, REMOTE_FEDERATION_NAME_2, 
                REMOTE_FEDERATION_DESCRIPTION_2, REMOTE_FEDERATION_ENABLED_2, 
                REMOTE_FEDERATION_OWNING_ADMIN_ID_2, REMOTE_FEDERATION_FHS_ID_2));
        
        this.synchronizationMechanism = Mockito.mock(SynchronizationMechanism.class);
        
        this.federationHost.setRemoteFederationsList(this.remoteFederations);
        this.federationHost.setSynchronizationMechanism(this.synchronizationMechanism);
    }
    
    @Before
    public void setUp() {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(propertiesHolder.getProperty(ConfigurationPropertyKeys.PROVIDER_ID_KEY)).thenReturn(PROVIDER_ID);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.databaseManager = Mockito.mock(DatabaseManager.class);
        Mockito.when(this.databaseManager.getFederationAdmins()).thenReturn(new ArrayList<FederationUser>());
        Mockito.when(this.databaseManager.getFederations()).thenReturn(new ArrayList<Federation>());
        
        this.federationHost = new FederationHost(databaseManager, communicationMechanism);
    }
    
    /*
     * 
     * FHSOperator
     * 
     */
    
    @Test
    public void testFederationAdminCreation() throws InvalidParameterException {
        String adminId1 = this.federationHost.addFederationAdmin(ADMIN_NAME_1, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
        String adminId2 = this.federationHost.addFederationAdmin(ADMIN_NAME_2, ADMIN_EMAIL_2, ADMIN_DESCRIPTION_2, ADMIN_ENABLED_2, USER_AUTHORIZATION_PROPERTIES);
        
        FederationUser returnedUser1 = this.federationHost.getFederationAdmin(adminId1);
        FederationUser returnedUser2 = this.federationHost.getFederationAdmin(adminId2);
        
        assertEquals(ADMIN_NAME_1, returnedUser1.getName());
        assertEquals(ADMIN_EMAIL_1, returnedUser1.getEmail());
        assertEquals(ADMIN_DESCRIPTION_1, returnedUser1.getDescription());
        assertEquals(ADMIN_ENABLED_1, returnedUser1.isEnabled());
        assertEquals(ADMIN_NAME_2, returnedUser2.getName());
        assertEquals(ADMIN_EMAIL_2, returnedUser2.getEmail());
        assertEquals(ADMIN_DESCRIPTION_2, returnedUser2.getDescription());
        assertEquals(ADMIN_ENABLED_2, returnedUser2.isEnabled());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationAdminWithNullUsername() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addFederationAdmin(null, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationAdminWithEmptyUsername() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addFederationAdmin("", ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationAdminWithAlreadyUsedUsername() throws InvalidParameterException {
        try {
            this.federationHost.addFederationAdmin(ADMIN_NAME_1, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
        } catch (InvalidParameterException e) {
            fail("Not expected to fail in the first call.");
        }
        
        this.federationHost.addFederationAdmin(ADMIN_NAME_1, ADMIN_EMAIL_2, ADMIN_DESCRIPTION_2, ADMIN_ENABLED_2, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetUnknownFederationAdmin() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getFederationAdmin(ADMIN_NAME_1);
    }
    
    @Test
    public void testGetFederationAdmins() throws FogbowException {
        setUpFederationData();
        
        List<FederationUser> admins = this.federationHost.getFederationAdmins();
        
        assertEquals(2, admins.size());
        assertEquals(this.admin1, admins.get(0));
        assertEquals(this.admin2, admins.get(1));
    }
    
    @Test
    public void testUpdateFederationAdmin() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateFederationAdmin(ADMIN_ID_1, UPDATED_ADMIN_NAME_1, UPDATED_ADMIN_EMAIL_1, 
                UPDATED_ADMIN_DESCRIPTION_1, UPDATED_ADMIN_ENABLED_1);
        
        assertEquals(UPDATED_ADMIN_NAME_1, this.admin1.getName());
        assertEquals(UPDATED_ADMIN_EMAIL_1, this.admin1.getEmail());
        assertEquals(UPDATED_ADMIN_DESCRIPTION_1, this.admin1.getDescription());
        assertEquals(UPDATED_ADMIN_ENABLED_1, this.admin1.isEnabled());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotUpdateInvalidFederationAdmin() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateFederationAdmin("invalidadminid", UPDATED_ADMIN_NAME_1, UPDATED_ADMIN_EMAIL_1, 
                UPDATED_ADMIN_DESCRIPTION_1, UPDATED_ADMIN_ENABLED_1);
    }
    
    @Test
    public void testDeleteFederationAdmin() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederationAdmin(ADMIN_ID_1);
        
        Mockito.verify(this.adminList).remove(admin1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteInvalidFederationAdmin() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederationAdmin("invalidadminid");
    }
    
    @Test
    public void testGetFederations() throws FogbowException {
        setUpFederationData();
        
        List<Federation> federations = this.federationHost.getFederations();
        
        assertEquals(3, federations.size());
        assertEquals(federation1, federations.get(0));
        assertEquals(federation2, federations.get(1));
        assertEquals(federation3, federations.get(2));
    }
    
    @Test
    public void testGetFederationsInstancesOwnedByAnotherMember() throws FogbowException {
        setUpFederationData();

        List<Federation> returnedFederationList = this.federationHost.getFederationsInstancesOwnedByAnotherMember(ADMIN_NAME_1);
        
        assertEquals(2, returnedFederationList.size());
        assertEquals(FEDERATION_ID_1, returnedFederationList.get(0).getId());
        assertEquals(FEDERATION_ID_3, returnedFederationList.get(1).getId());
    }
    
    @Test
    public void testUpdateFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateFederation(FEDERATION_ID_1, UPDATED_FEDERATION_ENABLED_1);
        
        assertFalse(this.federation1.enabled());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotUpdateInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateFederation("invalidfederationid", UPDATED_FEDERATION_ENABLED_1);
    }
    
    @Test
    public void testDeleteFederationInstance() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederationInstance(FEDERATION_ID_1);
        
        Mockito.verify(this.federationList).remove(federation1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederationInstance("invalidfederationid");
    }
    
    /*
     * 
     * Federations
     * 
     */
    
    @Test
    public void testCreateFederation() throws FogbowException {
        setUpFederationData();

        Federation returnedFederation = this.federationHost.createFederation(ADMIN_NAME_1, FEDERATION_NAME_1, 
                federationMetadata, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
        
        assertEquals(federation1, returnedFederation);
        
        Mockito.verify(federationList, Mockito.times(1)).add(federation1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotCreateFederation() throws FogbowException {
        setUpFederationData();

        this.federationHost.createFederation(REGULAR_USER_NAME_1, FEDERATION_NAME_1, federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationWithNullName() throws FogbowException {
        setUpFederationData();

        this.federationHost.createFederation(ADMIN_NAME_1, null, federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationWithEmptyName() throws FogbowException {
        setUpFederationData();

        this.federationHost.createFederation(ADMIN_NAME_1, "", federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test
    public void testGetFederation() throws FogbowException {
        setUpFederationData();
        
        Federation returnedFederation1 = this.federationHost.getFederation(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(this.federation1, returnedFederation1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetNonExistentFederation() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederation(ADMIN_NAME_1, "nonexistentid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederation() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederation(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test
    public void testDeleteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederation(ADMIN_NAME_1, FEDERATION_ID_1);
        
        Mockito.verify(this.federationList).remove(federation1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testDeleteNonExistentFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederation(ADMIN_NAME_1, "nonexistentid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotDeleteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederation(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerAdminCannotDeleteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteFederation(ADMIN_NAME_2, FEDERATION_ID_1);
    }
    
    @Test
    public void testGetRemoteFederationList() throws FogbowException {
        setUpFederationData();
        
        List<RemoteFederation> returnedRemoteFederations = this.federationHost.getRemoteFederationList(ADMIN_NAME_1);
        
        assertEquals(this.remoteFederations, returnedRemoteFederations);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetRemoteFederationList() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getRemoteFederationList(REGULAR_USER_NAME_1);
    }
    
    @Test
    public void testAddUserToAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addUserToAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, FEDERATION_ID_1);
        
        Mockito.verify(this.federation1).addRemoteUserAsAllowedFedAdmin(REMOTE_FEDERATION_ADMIN_ID_1, FHS_ID_2);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotAddUserToAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addUserToAllowedAdmins(REGULAR_USER_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonFederationOwnerAdminCannotAddUserToAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addUserToAllowedAdmins(ADMIN_NAME_2, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, FEDERATION_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddUserToAllowedAdminsPassingInvalidFederationId() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.addUserToAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, "invalidfederationid");
    }
    
    @Test
    public void testRemoveUserFromAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.removeUserFromAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, FHS_ID_2, FEDERATION_ID_1);
        
        Mockito.verify(this.federation1).removeRemoteUserFromAllowedAdmins(REMOTE_FEDERATION_ADMIN_ID_1, FHS_ID_2);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotRemoveUserFromAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.removeUserFromAllowedAdmins(REGULAR_USER_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonFederationOwnerAdminCannotRemoveUserFromAllowedAdmins() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.removeUserFromAllowedAdmins(ADMIN_NAME_2, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, FEDERATION_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRemoveUserFromAllowedAdminsPassingInvalidFederationId() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.removeUserFromAllowedAdmins(ADMIN_NAME_1, REMOTE_FEDERATION_ADMIN_ID_1, 
                FHS_ID_2, "invalidfederationid");
    }
    
    @Test
    public void testRequestToJoinRemoteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.requestToJoinRemoteFederation(ADMIN_NAME_1, REMOTE_FEDERATION_ID_1);
        
        Mockito.verify(this.communicationMechanism).joinRemoteFederation(admin1, REMOTE_FEDERATION_ID_1, FHS_ID_2);
        Mockito.verify(this.federationList).add(remoteFederation1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotRequestToJoinRemoteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.requestToJoinRemoteFederation(REGULAR_USER_NAME_1, REMOTE_FEDERATION_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRequestToJoinUnknownRemoteFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.requestToJoinRemoteFederation(ADMIN_NAME_1, "unknownremotefederationid");
    }
    
    /*
     * 
     * Membership
     * 
     */
    
    @Test
    public void testGrantMembership() throws FogbowException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_1, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_EMAIL_TO_GRANT_MEMBERSHIP, USER_DESCRIPTION_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
        
        Mockito.verify(this.federation1).addUser(USER_ID_TO_GRANT_MEMBERSHIP, USER_EMAIL_TO_GRANT_MEMBERSHIP, 
                USER_DESCRIPTION_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGrantMembershipOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.grantMembership(ADMIN_NAME_1, "invalidfederationid", USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_EMAIL_TO_GRANT_MEMBERSHIP, USER_DESCRIPTION_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGrantMembership() throws FogbowException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_2, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_EMAIL_TO_GRANT_MEMBERSHIP, USER_DESCRIPTION_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGrantMembership() throws FogbowException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_2, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_EMAIL_TO_GRANT_MEMBERSHIP, USER_DESCRIPTION_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test
    public void testGetFederationMembers() throws FogbowException {
        setUpFederationData();

        List<FederationUser> members = this.federationHost.getFederationMembers(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(2, members.size());
        assertEquals(REGULAR_USER_NAME_1, members.get(0).getName());
        assertEquals(REGULAR_USER_NAME_2, members.get(1).getName());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGetMembersFromInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getFederationMembers(ADMIN_NAME_1, "invalidfederationid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederationMembers() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederationMembers(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGetFederationMembers() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederationMembers(ADMIN_NAME_2, FEDERATION_ID_1);
    }
    
    @Test
    public void testGetFederationMemberInfo() throws FogbowException {
        setUpFederationData();

        FederationUser user = this.federationHost.getFederationMemberInfo(ADMIN_NAME_1, FEDERATION_ID_1, user1.getMemberId());
        
        assertEquals(REGULAR_USER_NAME_1, user.getName());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGetFederationMemberInfoFromInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getFederationMemberInfo(ADMIN_NAME_1, "invalidfederationid", user1.getMemberId());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederationMemberInfo() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederationMemberInfo(REGULAR_USER_NAME_1, FEDERATION_ID_1, user1.getMemberId());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGetFederationMemberInfo() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederationMemberInfo(ADMIN_NAME_2, FEDERATION_ID_1, user1.getMemberId());
    }
    
    @Test
    public void testRevokeMembership() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.revokeMembership(ADMIN_NAME_1, FEDERATION_ID_1, REGULAR_USER_ID_1);
        
        Mockito.verify(this.federation1).revokeMembership(REGULAR_USER_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRevokeMembershipFromInvalidFederation() throws FogbowException  {
        setUpFederationData();
        
        this.federationHost.revokeMembership(ADMIN_NAME_1, "invalidfederationid", REGULAR_USER_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotRevokeMembership() throws FogbowException {
        setUpFederationData();

        this.federationHost.revokeMembership(REGULAR_USER_NAME_1, FEDERATION_ID_1, user1.getMemberId());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotRevokeMembership() throws FogbowException {
        setUpFederationData();

        this.federationHost.revokeMembership(ADMIN_NAME_2, FEDERATION_ID_1, user1.getMemberId());
    }
    
    
    /*
     * 
     * Attributes
     * 
     */
    
    @Test
    public void testCreateAttribute() throws FogbowException {
        setUpFederationData();
        
        String returnedAttributeId = this.federationHost.createAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
        
        assertEquals(ATTRIBUTE_ID_1, returnedAttributeId);
        Mockito.verify(this.federation1).createAttribute(ATTRIBUTE_NAME_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateAttributeOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.createAttribute(ADMIN_NAME_1, "invalidfederationid", ATTRIBUTE_NAME_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotCreateAttributeIfUserDoesNotOwnFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.createAttribute(ADMIN_NAME_2, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
    }
    
    @Test
    public void testGetFederationAttributes() throws FogbowException {
        setUpFederationData();
        
        List<FederationAttribute> federationAttributes = this.federationHost.getFederationAttributes(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(2, federationAttributes.size());
        assertTrue(federationAttributes.contains(this.federationAttribute1));
        assertTrue(federationAttributes.contains(this.federationAttribute2));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGetFederationAttributesFromInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getFederationAttributes(ADMIN_NAME_1, "invalidfederationid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotGetFederationAttributesIfUserDoesNotOwnFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getFederationAttributes(ADMIN_NAME_2, FEDERATION_ID_1);
    }
    
    @Test
    public void testDeleteAttribute() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_ID_1);
        
        Mockito.verify(this.federation1).deleteAttribute(ATTRIBUTE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteAttributeFromInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteAttribute(ADMIN_NAME_1, "invalidfederationid", ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotDeleteAttribute() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteAttribute(REGULAR_USER_NAME_1, FEDERATION_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminOwnerCannotDeleteAttribute() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteAttribute(ADMIN_NAME_2, FEDERATION_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test
    public void testGrantAttribute() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.grantAttribute(ADMIN_NAME_1, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);

        Mockito.verify(this.federation1).grantAttribute(REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGrantAttributeOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.grantAttribute(ADMIN_NAME_1, "invalidfederationid", REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotGrantAttributeIfUserDoesNotOwnFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.grantAttribute(ADMIN_NAME_2, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test
    public void testRevokeAttribute() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.revokeAttribute(ADMIN_NAME_1, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);

        Mockito.verify(this.federation1).revokeAttribute(REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRevokeAttributeOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.revokeAttribute(ADMIN_NAME_1, "invalidfederationid", REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotRevokeAttributeIfUserDoesNotOwnFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.revokeAttribute(ADMIN_NAME_2, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    /*
     * 
     * Services
     * 
     */
    
    // TODO improve this test
    @Test
    public void testRegisterService() throws FogbowException {
        setUpFederationData();

        this.federationHost.registerService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
        
        Mockito.verify(this.federation1).registerService(SERVICE_OWNER_NAME_1, SERVICE_ENDPOINT_1,
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1, SERVICE_METADATA_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRegisterServiceOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.registerService(SERVICE_OWNER_NAME_1, "invalidfederationid", SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotRegisterService() throws FogbowException {
        setUpFederationData();

        this.federationHost.registerService(ADMIN_NAME_2, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
    }
    
    @Test
    public void testGetOwnedServices() throws FogbowException {
        setUpFederationData();

        List<String> servicesOwnedByAdmin1 = this.federationHost.getOwnedServices(SERVICE_OWNER_NAME_1, FEDERATION_ID_1);
        assertEquals(2, servicesOwnedByAdmin1.size());
        assertTrue(servicesOwnedByAdmin1.contains(SERVICE_ID_1));
        assertTrue(servicesOwnedByAdmin1.contains(SERVICE_ID_3));
        
        setUpFederationData();

        List<String> servicesOwnedByAdmin2 = this.federationHost.getOwnedServices(SERVICE_OWNER_NAME_2, FEDERATION_ID_1);
        assertEquals(1, servicesOwnedByAdmin2.size());
        assertTrue(servicesOwnedByAdmin2.contains(SERVICE_ID_2));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGetOwnedServicesOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getOwnedServices(SERVICE_OWNER_NAME_1, "invalidfederationid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotGetOwnedServices() throws FogbowException {
        setUpFederationData();

        this.federationHost.getOwnedServices(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test
    public void testGetOwnedService() throws FogbowException {
        setUpFederationData();

        FederationService federationService = this.federationHost.getOwnedService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1);
        assertEquals(SERVICE_ID_1, federationService.getServiceId());
        assertEquals(SERVICE_OWNER_NAME_1, federationService.getOwnerId());
        assertEquals(SERVICE_ENDPOINT_1, federationService.getEndpoint());
        assertEquals(this.discoveryPolicy1, federationService.getDiscoveryPolicy());
        assertEquals(this.invoker, federationService.getInvoker());
        assertEquals(SERVICE_METADATA_1, federationService.getMetadata());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotGetOwnedServiceOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.getOwnedService(SERVICE_OWNER_NAME_1, "invalidfederationid", SERVICE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotGetOwnedService() throws FogbowException {
        setUpFederationData();

        this.federationHost.getOwnedService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetOwnedServiceFailsIfServiceIdIsInvalid() throws FogbowException {
        setUpFederationData();

        this.federationHost.getOwnedService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, "invalidserviceid");
    }
    
    @Test
    public void testUpdateService() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_OWNER_NAME_1, 
                SERVICE_ID_1, updatedServiceMetadata, SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, 
                ACCESS_POLICY_CLASS_NAME);
        
        Mockito.verify(this.service1).update(updatedServiceMetadata, SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, ACCESS_POLICY_CLASS_NAME);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotUpdateServiceOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.updateService(SERVICE_OWNER_NAME_1, "invalidfederationid", SERVICE_OWNER_NAME_1, 
                SERVICE_ID_1, updatedServiceMetadata, SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, 
                ACCESS_POLICY_CLASS_NAME);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotUpdateService() throws FogbowException {
        setUpFederationData();

        this.federationHost.updateService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_OWNER_NAME_1, 
                SERVICE_ID_1, updatedServiceMetadata, SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, 
                ACCESS_POLICY_CLASS_NAME);
    }
    
    @Test
    public void testDeleteService() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_OWNER_NAME_1, SERVICE_ID_1);
        
        Mockito.verify(this.federation1).deleteService(SERVICE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotDeleteServiceOnInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.deleteService(SERVICE_OWNER_NAME_1, "invalidfederationid", SERVICE_OWNER_NAME_1, SERVICE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotDeleteService() throws FogbowException {
        setUpFederationData();

        this.federationHost.deleteService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_OWNER_NAME_1, SERVICE_ID_1);
    }
    
    @Test
    public void testGetAuthorizedServices() throws FogbowException {
        setUpFederationData();

        List<FederationService> services = this.federationHost.getAuthorizedServices(REGULAR_USER_NAME_1, FEDERATION_ID_1);
        assertEquals(2, services.size());
        
        assertEquals(service1, services.get(0));
        assertEquals(service2, services.get(1));
    }
    
    @Test
    public void testGetOwnedFederations() throws FogbowException {
        setUpFederationData();

        List<Federation> federationsAdmin1 = this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_1);
        assertEquals(2, federationsAdmin1.size());
        assertTrue(federationsAdmin1.contains(federation1));
        assertTrue(federationsAdmin1.contains(federation3));

        setUpFederationData();
        
        List<Federation> federationsAdmin2 = this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_2);
        assertEquals(1, federationsAdmin2.size());
        assertTrue(federationsAdmin2.contains(federation2));
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetOwnedFederations() throws FogbowException {
        setUpFederationData();

        this.federationHost.getFederationsOwnedByUser(REGULAR_USER_NAME_1);
    }
    
    @Test
    public void testInvokeService() throws FogbowException {
        setUpFederationData();

        this.federationHost.invokeService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1, HttpMethod.GET, 
                new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, Object>());
        
        Mockito.verify(this.federation1).invoke(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1, 
                HttpMethod.GET, new ArrayList<String>(), new HashMap<String, String>(), 
                new HashMap<String, Object>());
    }
    
    /*
     * 
     * Authentication
     * 
     */
    
    @Test
    public void testFederationAdminLogin() throws FogbowException {
        setUpFederationData();
        
        String returnedToken = this.federationHost.federationAdminLogin(ADMIN_ID_1, adminCredentials1);
        
        assertEquals(ADMIN_TOKEN_1, returnedToken);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testFederationAdminLoginInvalidAdmin() throws FogbowException {
        setUpFederationData();
        
        String returnedToken = this.federationHost.login(null, "invalidAdminId", adminCredentials1);
        
        assertEquals(ADMIN_TOKEN_1, returnedToken);
    }
    
    @Test
    public void testNonFederationAdminLogin() throws FogbowException {
        setUpFederationData();
        
        String returnedToken = this.federationHost.login(FEDERATION_ID_1, REGULAR_USER_ID_1, regularUserCredentials1);
        
        assertEquals(REGULAR_USER_TOKEN_1, returnedToken);
        
        Mockito.verify(this.federation1).login(REGULAR_USER_ID_1, regularUserCredentials1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testNonFederationAdminLoginInvalidFederationId() throws FogbowException {
        setUpFederationData();
        
        this.federationHost.login("invalidFederationId", REGULAR_USER_ID_1, regularUserCredentials1);
    }
    
    /*
     * 
     * Authorization
     * 
     */
    
    @Test
    public void testMap() throws FogbowException {
        setUpFederationData();
        
        Map<String, String> responseCredentials = this.federationHost.map(FEDERATION_ID_1, SERVICE_ID_1, REGULAR_USER_ID_1, CLOUD_NAME);
        
        assertEquals(credentialsMapCloud1, responseCredentials);
    }
    
    /*
     * 
     * Remote Federations
     * 
     */
    
    @Test
    public void testUpdateRemoteFederationList() throws FogbowException {
        setUpFederationData();
        
        List<RemoteFederation> federationsToUpdate = new ArrayList<RemoteFederation>();
        
        RemoteFederation updatedRemoteFederation1 = new RemoteFederation(REMOTE_FEDERATION_ID_1, 
                UPDATED_REMOTE_FEDERATION_NAME_1, REMOTE_FEDERATION_DESCRIPTION_1, REMOTE_FEDERATION_ENABLED_1, 
                REMOTE_FEDERATION_OWNING_ADMIN_ID_1, REMOTE_FEDERATION_FHS_ID_1);
        RemoteFederation updatedRemoteFederation2 = new RemoteFederation(REMOTE_FEDERATION_ID_2, 
                REMOTE_FEDERATION_NAME_2, UPDATED_REMOTE_FEDERATION_DESCRIPTION_2, REMOTE_FEDERATION_ENABLED_2, 
                REMOTE_FEDERATION_OWNING_ADMIN_ID_2, REMOTE_FEDERATION_FHS_ID_2);
        
        federationsToUpdate.add(updatedRemoteFederation1);
        federationsToUpdate.add(updatedRemoteFederation2);
        
        this.federationHost.updateRemoteFederationList(FHS_ID_2, federationsToUpdate);
        
        List<RemoteFederation> updatedRemoteFederationList = this.federationHost.getRemoteFederationList(ADMIN_NAME_1);
        
        assertEquals(2, updatedRemoteFederationList.size());
        assertTrue(updatedRemoteFederationList.contains(updatedRemoteFederation1));
        assertTrue(updatedRemoteFederationList.contains(updatedRemoteFederation2));
    }
    
    @Test
    public void testJoinRemoteFederation() throws FogbowException {
        setUpFederationData();
        
        FederationUser remoteFederationUser = Mockito.mock(FederationUser.class);
        
        Federation returnedFederation = this.federationHost.joinRemoteFederation(remoteFederationUser, FHS_ID_2, FEDERATION_ID_1);
        
        Mockito.verify(this.federation1).addRemoteAdmin(remoteFederationUser, FHS_ID_2);
        assertEquals(this.federation1, returnedFederation);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotJoinInvalidFederation() throws FogbowException {
        setUpFederationData();
        
        FederationUser remoteFederationUser = Mockito.mock(FederationUser.class);
        
        this.federationHost.joinRemoteFederation(remoteFederationUser, FHS_ID_2, "invalidfederationid");
    }
}
