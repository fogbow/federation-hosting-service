package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.fhs.core.plugins.access.ServiceAccessPolicy;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPlugin;
import cloud.fogbow.fhs.core.plugins.authentication.FederationAuthenticationPluginInstantiator;

// TODO documentation
public class FederationTest {
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final Map<String, String> FEDERATION_METADATA_1 = new HashMap<String, String>();
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
    private static final String NOT_REGISTERED_USER_ID = "notRegisteredUserId";
    private static final String FEDERATION_SERVICE_ID_1 = "federationServiceId1";
    private static final String FEDERATION_SERVICE_ID_2 = "federationServiceId2";
    private static final String ATTRIBUTE_ID_1 = "attributeId1";
    private static final String ATTRIBUTE_NAME_1 = "attributeName1";
    private static final String ATTRIBUTE_ID_2 = "attributeId2";
    private static final String ATTRIBUTE_NAME_2 = "attributeName2";
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
    private List<FederationUser> federationMembers;
    private List<FederationService> federationServices;
    private List<FederationAttribute> federationAttributes;
    private Federation federation;
    private FederationUser federationUser1;
    private FederationUser federationUser2;
    private FederationService federationService1;
    private FederationService federationService2;
    private FederationAttribute federationAttribute1;
    private FederationAttribute federationAttribute2;
    private Map<String, String> federationUserCredentials1;
    private Map<String, String> federationUserCredentials2;
    private FederationAuthenticationPluginInstantiator authenticationPluginInstantiator;
    private FederationAuthenticationPlugin authenticationPlugin;
    private ServiceAccessPolicy accessPolicy;
    private Map<String, String> credentials;
    private Map<String, String> serviceMetadata;
    private FederationServiceFactory federationServiceFactory;
    
    @Before
    public void setUp() throws Exception {
        serviceMetadata = new HashMap<String, String>();
        
        this.federationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, FEDERATION_ID_1,
                FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        this.federationUser2 = new FederationUser(FEDERATION_USER_ID_2, FEDERATION_USER_NAME_2, FEDERATION_ID_1,
                FEDERATION_USER_EMAIL_2, FEDERATION_USER_DESCRIPTION_2, FEDERATION_USER_ENABLED_2, 
                new ArrayList<String>(), IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.credentials = new HashMap<String, String>();
        this.credentials.put(CREDENTIALS_KEY, CREDENTIALS_VALUE);
        
        this.accessPolicy = Mockito.mock(ServiceAccessPolicy.class);
        Mockito.when(this.accessPolicy.getCredentialsForAccess(federationUser1, CLOUD_NAME)).thenReturn(credentials);
        
        this.federationService1 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService1.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_1);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser1)).thenReturn(true);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser2)).thenReturn(false);
        Mockito.when(this.federationService1.getAccessPolicy()).thenReturn(accessPolicy);
        this.federationService2 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService2.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_2);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser1)).thenReturn(false);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser2)).thenReturn(true);
        
        this.federationServices = new ArrayList<FederationService>();
        this.federationServices.add(federationService1);
        this.federationServices.add(federationService2);
        
        this.federationAttribute1 = new FederationAttribute(ATTRIBUTE_ID_1, ATTRIBUTE_NAME_1);
        this.federationAttribute2 = new FederationAttribute(ATTRIBUTE_ID_2, ATTRIBUTE_NAME_2);
        
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
                FEDERATION_METADATA_1)).thenReturn(federationService1);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
    }
    
    @Test
    public void testAddUser() throws InvalidParameterException {
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationServices = new ArrayList<FederationService>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
        
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
        assertEquals(2, this.federationMembers.size());
        
        this.federation.revokeMembership(FEDERATION_USER_ID_1);
        
        assertEquals(1, this.federationMembers.size());
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
    public void testRegisterAndGetServices() {
        this.federationServices = new ArrayList<FederationService>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator,
                this.federationServiceFactory);
        
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
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
        
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
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
        
        List<FederationAttribute> attributesBeforeCreation = this.federation.getAttributes();
        
        assertTrue(attributesBeforeCreation.isEmpty());
        
        String returnedAttributeId = this.federation.createAttribute(ATTRIBUTE_NAME_1);
        
        assertNotNull(returnedAttributeId);
        
        List<FederationAttribute> attributesAfterCreation = this.federation.getAttributes();
        
        assertEquals(1, attributesAfterCreation.size());
        assertEquals(ATTRIBUTE_NAME_1, attributesAfterCreation.get(0).getName());
        assertEquals(returnedAttributeId, attributesAfterCreation.get(0).getId());
    }
    
    @Test
    public void testDeleteAttribute() throws InvalidParameterException {
        assertEquals(2, this.federationAttributes.size());
        
        this.federation.deleteAttribute(ATTRIBUTE_ID_1);
        
        assertEquals(1, this.federationAttributes.size());
        assertTrue(this.federationAttributes.contains(this.federationAttribute2));
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
                FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                federationUser1Attributes, IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
        
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
                FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1, 
                federationUser1Attributes, IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), 
                false, false);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices, 
                this.federationAttributes, this.authenticationPluginInstantiator, 
                this.federationServiceFactory);
        
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
}
