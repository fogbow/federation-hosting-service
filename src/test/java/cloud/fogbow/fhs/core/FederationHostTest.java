package cloud.fogbow.fhs.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationAttribute;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceOperation;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.utils.JsonUtils;
import cloud.fogbow.fhs.core.utils.TestUtils;

public class FederationHostTest {
    private static final String ADMIN_NAME_1 = "admin1";
    private static final String ADMIN_NAME_2 = "admin2";
    private static final String ADMIN_EMAIL_1 = "adminEmail1";
    private static final String ADMIN_EMAIL_2 = "adminEmail2";
    private static final String ADMIN_DESCRIPTION_1 = "adminDescription1";
    private static final String ADMIN_DESCRIPTION_2 = "adminDescription2";
    private static final boolean ADMIN_ENABLED_1 = true;
    private static final boolean ADMIN_ENABLED_2 = true;
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_ID_3 = "federationId3";
    private static final String FEDERATION_NAME_1 = "federation1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final boolean FEDERATION_ENABLED_1 = true;
    private static final String USER_ID_TO_GRANT_MEMBERSHIP = "userIdToGrantMembership";
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
    private DiscoveryPolicyInstantiator discoveryPolicyInstantiator;
    private ServiceInvokerInstantiator serviceInvokerInstantiator;
    private AccessPolicyInstantiator accessPolicyInstantiator;
    private JsonUtils jsonUtils;
    private ServiceAccessPolicy accessPolicy;
    private FederationAttribute federationAttribute1;
    private FederationAttribute federationAttribute2;
    
    private void setUpFederationData() throws InvalidParameterException {
        this.invoker = Mockito.mock(ServiceInvoker.class);
        
        this.admin1 = new FederationUser(ADMIN_NAME_1, FEDERATION_ID_1, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
        this.admin2 = new FederationUser(ADMIN_NAME_2, FEDERATION_ID_1, ADMIN_EMAIL_2, ADMIN_DESCRIPTION_2, ADMIN_ENABLED_2, USER_AUTHORIZATION_PROPERTIES);
        this.user1 = new FederationUser(REGULAR_USER_ID_1, REGULAR_USER_NAME_1, FEDERATION_ID_1, REGULAR_USER_EMAIL_1, 
                REGULAR_USER_DESCRIPTION_1, REGULAR_USER_ENABLED_1, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, USER_AUTHORIZATION_PROPERTIES);
        this.user2 = new FederationUser(REGULAR_USER_ID_2, REGULAR_USER_NAME_2, FEDERATION_ID_1, REGULAR_USER_EMAIL_2, 
                REGULAR_USER_DESCRIPTION_2, REGULAR_USER_ENABLED_2, new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, USER_AUTHORIZATION_PROPERTIES);

        this.discoveryPolicy1 = Mockito.mock(ServiceDiscoveryPolicy.class);
        Mockito.when(discoveryPolicy1.isDiscoverableBy(user1)).thenReturn(true);
        Mockito.when(discoveryPolicy1.isDiscoverableBy(user2)).thenReturn(false);
        
        this.accessPolicy = Mockito.mock(ServiceAccessPolicy.class);
        Mockito.when(this.accessPolicy.isAllowedToPerform(user1, new ServiceOperation(HttpMethod.GET))).thenReturn(true);
        Mockito.when(this.accessPolicy.isAllowedToPerform(user2, new ServiceOperation(HttpMethod.GET))).thenReturn(false);
        
        this.discoveryPolicyInstantiator = Mockito.mock(DiscoveryPolicyInstantiator.class);
        Mockito.when(this.discoveryPolicyInstantiator.getDiscoveryPolicy(SERVICE_DISCOVERY_POLICY_CLASS_NAME_1)).thenReturn(discoveryPolicy1);
        
        this.serviceInvokerInstantiator = Mockito.mock(ServiceInvokerInstantiator.class);
        Mockito.when(this.serviceInvokerInstantiator.getInvoker(SERVICE_INVOKER_CLASS_NAME_1, SERVICE_METADATA_1, FEDERATION_ID_1)).thenReturn(invoker);
        
        this.accessPolicyInstantiator = Mockito.mock(AccessPolicyInstantiator.class);
        Mockito.when(this.accessPolicyInstantiator.getAccessPolicy(ACCESS_POLICY_CLASS_NAME, SERVICE_METADATA_1)).thenReturn(this.accessPolicy);
        
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
        Mockito.when(federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(federation1.getUserById(REGULAR_USER_NAME_1)).thenReturn(user1);
        Mockito.when(federation1.getUserByMemberId(REGULAR_USER_ID_1)).thenReturn(user1);
        Mockito.when(federation1.getOwner()).thenReturn(ADMIN_NAME_1);
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
        
        this.federation2 = Mockito.mock(Federation.class);
        Mockito.when(federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(federation2.getOwner()).thenReturn(ADMIN_NAME_2);
        
        this.federation3 = Mockito.mock(Federation.class);
        Mockito.when(federation3.getId()).thenReturn(FEDERATION_ID_3);
        Mockito.when(federation3.getOwner()).thenReturn(ADMIN_NAME_1);
        
        this.adminList = TestUtils.getMockedList(admin1, admin2);
        this.federationList = TestUtils.getMockedList(federation1, federation2, federation3);
        
        this.jsonUtils = Mockito.mock(JsonUtils.class);
        Mockito.when(jsonUtils.fromJson(CREDENTIALS_STRING, Map.class)).thenReturn(credentialsMap);
        
        this.federationHost = new FederationHost(adminList, federationList, serviceInvokerInstantiator, 
                discoveryPolicyInstantiator, accessPolicyInstantiator, jsonUtils);
    }
    
    @Before
    public void setUp() {
        this.federationHost = new FederationHost();
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
    public void testCannotCreateFederationAdminWithNullUsername() throws InvalidParameterException {
        setUpFederationData();
        
        this.federationHost.addFederationAdmin(null, ADMIN_EMAIL_1, ADMIN_DESCRIPTION_1, ADMIN_ENABLED_1, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationAdminWithEmptyUsername() throws InvalidParameterException {
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
    public void testGetUnknownFederationAdmin() throws InvalidParameterException {
        setUpFederationData();
        
        this.federationHost.getFederationAdmin(ADMIN_NAME_1);
    }
    
    /*
     * 
     * Federations
     * 
     */
    
    @Test
    public void testCreateFederation() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        Federation federation = this.federationHost.createFederation(ADMIN_NAME_1, FEDERATION_NAME_1, 
                federationMetadata, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
        
        assertEquals(ADMIN_NAME_1, federation.getOwner());
        assertEquals(FEDERATION_NAME_1, federation.getName());
        assertEquals(FEDERATION_DESCRIPTION_1, federation.getDescription());
        assertEquals(FEDERATION_ENABLED_1, federation.enabled());
        
        Mockito.verify(federationList, Mockito.times(1)).add(Mockito.any(Federation.class));
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotCreateFederation() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.createFederation(REGULAR_USER_NAME_1, FEDERATION_NAME_1, federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationWithNullName() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.createFederation(ADMIN_NAME_1, null, federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotCreateFederationWithEmptyName() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.createFederation(ADMIN_NAME_1, "", federationMetadata, 
                FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1);
    }
    
    @Test
    public void testGetFederation() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        Federation returnedFederation1 = this.federationHost.getFederation(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(this.federation1, returnedFederation1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetNonExistentFederation() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederation(ADMIN_NAME_1, "nonexistentid");
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederation() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();

        this.federationHost.getFederation(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    /*
     * 
     * Membership
     * 
     */
    
    @Test
    public void testGrantMembership() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_1, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_AUTHORIZATION_PROPERTIES);
        
        Mockito.verify(this.federation1).addUser(USER_ID_TO_GRANT_MEMBERSHIP, USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGrantMembership() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_2, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGrantMembership() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.grantMembership(ADMIN_NAME_2, FEDERATION_ID_1, USER_ID_TO_GRANT_MEMBERSHIP, 
                USER_AUTHORIZATION_PROPERTIES);
    }
    
    @Test
    public void testGetFederationMembers() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        List<FederationUser> members = this.federationHost.getFederationMembers(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(2, members.size());
        assertEquals(REGULAR_USER_NAME_1, members.get(0).getName());
        assertEquals(REGULAR_USER_NAME_2, members.get(1).getName());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederationMembers() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederationMembers(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGetFederationMembers() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederationMembers(ADMIN_NAME_2, FEDERATION_ID_1);
    }
    
    @Test
    public void testGetFederationMemberInfo() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        FederationUser user = this.federationHost.getFederationMemberInfo(ADMIN_NAME_1, FEDERATION_ID_1, user1.getMemberId());
        
        assertEquals(REGULAR_USER_NAME_1, user.getName());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonAdminUserCannotGetFederationMemberInfo() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederationMemberInfo(REGULAR_USER_NAME_1, FEDERATION_ID_1, user1.getMemberId());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonOwnerUserCannotGetFederationMemberInfo() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederationMemberInfo(ADMIN_NAME_2, FEDERATION_ID_1, user1.getMemberId());
    }
    
    /*
     * 
     * Attributes
     * 
     */
    
    @Test
    public void testCreateAttribute() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        String returnedAttributeId = this.federationHost.createAttribute(ADMIN_NAME_1, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
        
        assertEquals(ATTRIBUTE_ID_1, returnedAttributeId);
        Mockito.verify(this.federation1).createAttribute(ATTRIBUTE_NAME_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotCreateAttributeIfUserDoesNotOwnFederation() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        this.federationHost.createAttribute(ADMIN_NAME_2, FEDERATION_ID_1, ATTRIBUTE_NAME_1);
    }
    
    @Test
    public void testGetFederationAttributes() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        List<FederationAttribute> federationAttributes = this.federationHost.getFederationAttributes(ADMIN_NAME_1, FEDERATION_ID_1);
        
        assertEquals(2, federationAttributes.size());
        assertTrue(federationAttributes.contains(this.federationAttribute1));
        assertTrue(federationAttributes.contains(this.federationAttribute2));
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotGetFederationAttributesIfUserDoesNotOwnFederation() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();
        
        this.federationHost.getFederationAttributes(ADMIN_NAME_2, FEDERATION_ID_1);
    }
    
    @Test
    public void testGrantAttribute() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        this.federationHost.grantAttribute(ADMIN_NAME_1, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);

        Mockito.verify(this.federation1).grantAttribute(REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotGrantAttributeIfUserDoesNotOwnFederation() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        this.federationHost.grantAttribute(ADMIN_NAME_2, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test
    public void testRevokeAttribute() throws InvalidParameterException, UnauthorizedRequestException {
        setUpFederationData();
        
        this.federationHost.revokeAttribute(ADMIN_NAME_1, FEDERATION_ID_1, REGULAR_USER_ID_1, ATTRIBUTE_ID_1);

        Mockito.verify(this.federation1).revokeAttribute(REGULAR_USER_ID_1, ATTRIBUTE_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testCannotRevokeAttributeIfUserDoesNotOwnFederation() throws InvalidParameterException, UnauthorizedRequestException {
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
    public void testRegisterService() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.registerService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
        
        Mockito.verify(this.federation1).registerService(Mockito.any(FederationService.class));
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotRegisterService() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.registerService(ADMIN_NAME_2, FEDERATION_ID_1, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRegisterServiceWithNullEndpoint() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.registerService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, null, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRegisterServiceWithEmptyEndpoint() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.registerService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, "", SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME_1, SERVICE_INVOKER_CLASS_NAME_1);
    }
    
    @Test
    public void testGetOwnedServices() throws UnauthorizedRequestException, InvalidParameterException {
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
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotGetOwnedServices() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getOwnedServices(REGULAR_USER_NAME_1, FEDERATION_ID_1);
    }
    
    @Test
    public void testGetOwnedService() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        FederationService federationService = this.federationHost.getOwnedService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1);
        assertEquals(SERVICE_ID_1, federationService.getServiceId());
        assertEquals(SERVICE_OWNER_NAME_1, federationService.getOwnerId());
        assertEquals(SERVICE_ENDPOINT_1, federationService.getEndpoint());
        assertEquals(this.discoveryPolicy1, federationService.getDiscoveryPolicy());
        assertEquals(this.invoker, federationService.getInvoker());
        assertEquals(SERVICE_METADATA_1, federationService.getMetadata());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testNonServiceOwnerUserCannotGetOwnedService() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getOwnedService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1);
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetOwnedServiceFailsIfServiceIdIsInvalid() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getOwnedService(SERVICE_OWNER_NAME_1, FEDERATION_ID_1, "invalidserviceid");
    }
    
    @Test
    public void testGetAuthorizedServices() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        List<FederationService> services = this.federationHost.getAuthorizedServices(REGULAR_USER_NAME_1, FEDERATION_ID_1);
        assertEquals(2, services.size());
        
        assertEquals(service1, services.get(0));
        assertEquals(service2, services.get(1));
    }
    
    @Test
    public void testGetOwnedFederations() throws UnauthorizedRequestException, InvalidParameterException {
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
    public void testNonAdminUserCannotGetOwnedFederations() throws UnauthorizedRequestException, InvalidParameterException {
        setUpFederationData();

        this.federationHost.getFederationsOwnedByUser(REGULAR_USER_NAME_1);
    }
    
    @Test
    public void testInvokeService() throws FogbowException {
        setUpFederationData();

        this.federationHost.invokeService(REGULAR_USER_NAME_1, FEDERATION_ID_1, SERVICE_ID_1, HttpMethod.GET, 
                new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, Object>());
        
        Mockito.verify(service1).invoke(user1, HttpMethod.GET, new ArrayList<String>(), 
                new HashMap<String, String>(), new HashMap<String, Object>());
    }
    
    @Test
    public void testInvokeServiceUserIsNotAuthorizedToInvoke() throws FogbowException {
        setUpFederationData();

        try {
            this.federationHost.invokeService(REGULAR_USER_NAME_2, FEDERATION_ID_1, SERVICE_ID_1, HttpMethod.GET,
                    new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, Object>());
            Assert.fail("Expected UnauthorizedRequestException.");
        } catch (UnauthorizedRequestException e) {
            
        }
        
        Mockito.verify(service1, Mockito.never()).
            invoke(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }
    
    /*
     * 
     * Authorization
     * 
     */
    
    @Test
    public void testMap() throws InvalidParameterException {
        setUpFederationData();
        
        Map<String, String> responseCredentials = this.federationHost.map(FEDERATION_ID_1, CLOUD_NAME);
        
        assertEquals(credentialsMapCloud1, responseCredentials);
    }
    
}
