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

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.fhs.api.http.response.FederationDescription;
import cloud.fogbow.fhs.api.http.response.FederationId;
import cloud.fogbow.fhs.api.http.response.FederationMember;
import cloud.fogbow.fhs.api.http.response.MemberId;
import cloud.fogbow.fhs.api.http.response.RequestResponse;
import cloud.fogbow.fhs.api.http.response.ServiceDiscovered;
import cloud.fogbow.fhs.api.http.response.ServiceId;
import cloud.fogbow.fhs.api.http.response.ServiceInfo;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationService;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.plugins.discovery.ServiceDiscoveryPolicy;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvoker;
import cloud.fogbow.fhs.core.plugins.response.DefaultServiceResponse;

// TODO add checks to authorization
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FhsPublicKeysHolder.class , AuthenticationUtil.class })
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
    
    private ApplicationFacade applicationFacade;
    private FhsPublicKeysHolder publicKeysHolder;
    private AuthorizationPlugin<FhsOperation> authorizationPlugin;
    private FederationHost federationHost;
    private RSAPublicKey asPublicKey;
    private Federation federation1;
    private Federation federation2;
    private SystemUser systemUser1;
    private SystemUser systemUser2;
    private FederationUser federationUser1;
    private FederationUser federationUser2;
    private FederationService federationService;
    private FederationService federationService2; 
    private ServiceDiscoveryPolicy serviceDiscoveryPolicy;
    private ServiceInvoker serviceInvoker;
    
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
        
        asPublicKey = Mockito.mock(RSAPublicKey.class);
        
        this.publicKeysHolder = Mockito.mock(FhsPublicKeysHolder.class);
        Mockito.when(this.publicKeysHolder.getAsPublicKey()).thenReturn(asPublicKey);
        
        PowerMockito.mockStatic(FhsPublicKeysHolder.class);
        BDDMockito.given(FhsPublicKeysHolder.getInstance()).willReturn(publicKeysHolder);
        
        systemUser1 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser1.getId()).thenReturn(ADMIN_NAME);
        systemUser2 = Mockito.mock(SystemUser.class);
        Mockito.when(systemUser2.getId()).thenReturn(ADMIN_NAME_2);
        
        PowerMockito.mockStatic(AuthenticationUtil.class);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1)).willReturn(systemUser1);
        BDDMockito.given(AuthenticationUtil.authenticate(asPublicKey, TOKEN_2)).willReturn(systemUser2);
        
        this.authorizationPlugin = Mockito.mock(AuthorizationPlugin.class);
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.addFederationAdmin(ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED)).thenReturn(ADMIN_ID);
        Mockito.when(this.federationHost.createFederation(ADMIN_NAME, FEDERATION_NAME_1, 
                FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, FEDERATION_ENABLED_1)).thenReturn(federation1);
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME, ADMIN_NAME)).
                thenReturn(Arrays.asList(federation1, federation2));
        Mockito.when(this.federationHost.getFederationsOwnedByUser(ADMIN_NAME_2, ADMIN_NAME_2)).thenReturn(Arrays.asList());
        Mockito.when(this.federationHost.grantMembership(ADMIN_NAME, FEDERATION_ID_1, USER_ID_TO_ADD)).thenReturn(federationUser1);
        Mockito.when(this.federationHost.getFederationMembers(ADMIN_NAME, FEDERATION_ID_1)).
                thenReturn(Arrays.asList(federationUser1, federationUser2));
        Mockito.when(this.federationHost.registerService(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ENDPOINT_1, SERVICE_METADATA_1, 
                SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME)).thenReturn(SERVICE_ID_1);
        Mockito.when(this.federationHost.getOwnedServices(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME)).
                thenReturn(Arrays.asList(SERVICE_ID_1, SERVICE_ID_2));
        Mockito.when(this.federationHost.getOwnedService(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ID_1)).
                thenReturn(federationService);
        Mockito.when(this.federationHost.getAuthorizedServices(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME)).
                thenReturn(Arrays.asList(federationService, federationService2));
        Mockito.when(this.federationHost.invokeService(ADMIN_NAME, FEDERATION_ID_1, SERVICE_ID_1, 
                HttpMethod.GET, PATH, HEADERS, BODY)).thenReturn(new DefaultServiceResponse(RESPONSE_CODE, RESPONSE_DATA));
        Mockito.when(this.federationHost.map(FEDERATION_ID_1, CLOUD_NAME)).thenReturn(CREDENTIALS);
        
        applicationFacade = ApplicationFacade.getInstance();
        
        applicationFacade.setAsPublicKey(null);
        applicationFacade.setAuthorizationPlugin(authorizationPlugin);
        applicationFacade.setLocalFederationHost(federationHost);
    }
    
    @Test
    public void testAddFederationAdmin() throws FogbowException {
        String returnedAdminId = this.applicationFacade.addFederationAdmin(TOKEN_1, ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED);
        
        assertEquals(ADMIN_ID, returnedAdminId);
        
        Mockito.verify(this.federationHost).addFederationAdmin(ADMIN_NAME, ADMIN_EMAIL, 
                ADMIN_DESCRIPTION, ADMIN_ENABLED);
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
        
        Mockito.verify(this.federationHost).getFederationsOwnedByUser(ADMIN_NAME, ADMIN_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testListFederationsNoFederation() throws FogbowException {
        List<FederationDescription> federationsDescriptions = this.applicationFacade.listFederations(TOKEN_2, ADMIN_NAME_2);
        
        assertTrue(federationsDescriptions.isEmpty());
        
        Mockito.verify(this.federationHost).getFederationsOwnedByUser(ADMIN_NAME_2, ADMIN_NAME_2);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_2));
    }
    
    @Test
    public void testGrantMembership() throws FogbowException {
        MemberId memberId = this.applicationFacade.grantMembership(TOKEN_1, FEDERATION_ID_1, USER_ID_TO_ADD);
        
        assertEquals(MEMBER_ID_1, memberId.getMemberId());
        
        Mockito.verify(this.federationHost).grantMembership(ADMIN_NAME, FEDERATION_ID_1, USER_ID_TO_ADD);
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
    public void testRegisterService() throws FogbowException {
        ServiceId serviceId = this.applicationFacade.registerService(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        
        assertEquals(SERVICE_ID_1, serviceId.getServiceId());
        
        Mockito.verify(this.federationHost).registerService(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ENDPOINT_1, 
                SERVICE_METADATA_1, SERVICE_DISCOVERY_POLICY_CLASS_NAME, SERVICE_ACCESS_POLICY_CLASS_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
    
    @Test
    public void testGetServices() throws FogbowException {
        List<ServiceId> serviceIds = this.applicationFacade.getServices(TOKEN_1, FEDERATION_ID_1, ADMIN_NAME);
        
        assertEquals(2, serviceIds.size());
        assertEquals(SERVICE_ID_1, serviceIds.get(0).getServiceId());
        assertEquals(SERVICE_ID_2, serviceIds.get(1).getServiceId());
        
        Mockito.verify(this.federationHost).getOwnedServices(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME);
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
        
        Mockito.verify(this.federationHost).getOwnedService(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME, SERVICE_ID_1);
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
        
        Mockito.verify(this.federationHost).getAuthorizedServices(ADMIN_NAME, FEDERATION_ID_1, ADMIN_NAME);
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
    public void testMap() throws FogbowException {
        Map<String, String> response = this.applicationFacade.map(TOKEN_1, FEDERATION_ID_1, CLOUD_NAME);
        
        assertEquals(response, CREDENTIALS);
        
        Mockito.verify(this.federationHost).map(FEDERATION_ID_1, CLOUD_NAME);
        BDDMockito.verify(AuthenticationUtil.authenticate(asPublicKey, TOKEN_1));
    }
}
