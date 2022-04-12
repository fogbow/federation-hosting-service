package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.common.exceptions.InvalidParameterException;

public class FederationTest {
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final Map<String, String> FEDERATION_METADATA_1 = new HashMap<String, String>();
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription";
    private static final boolean FEDERATION_ENABLED = true;
    private static final String USER_ID_TO_ADD = "userIdToAdd";
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
    private List<FederationUser> federationMembers;
    private List<FederationService> federationServices;
    private Federation federation;
    private FederationUser federationUser1;
    private FederationUser federationUser2;
    private FederationService federationService1;
    private FederationService federationService2;
    
    @Before
    public void setUp() {
        this.federationUser1 = new FederationUser(FEDERATION_USER_ID_1, FEDERATION_USER_NAME_1, 
                FEDERATION_USER_EMAIL_1, FEDERATION_USER_DESCRIPTION_1, FEDERATION_USER_ENABLED_1);
        this.federationUser2 = new FederationUser(FEDERATION_USER_ID_2, FEDERATION_USER_NAME_2, 
                FEDERATION_USER_EMAIL_2, FEDERATION_USER_DESCRIPTION_2, FEDERATION_USER_ENABLED_2);
        
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationMembers.add(this.federationUser1);
        this.federationMembers.add(this.federationUser2);
        
        this.federationService1 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService1.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_1);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser1)).thenReturn(true);
        Mockito.when(this.federationService1.isDiscoverableBy(federationUser2)).thenReturn(false);
        this.federationService2 = Mockito.mock(FederationService.class);
        Mockito.when(this.federationService2.getServiceId()).thenReturn(FEDERATION_SERVICE_ID_2);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser1)).thenReturn(false);
        Mockito.when(this.federationService2.isDiscoverableBy(federationUser2)).thenReturn(true);
        
        this.federationServices = new ArrayList<FederationService>();
        this.federationServices.add(federationService1);
        this.federationServices.add(federationService2);
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices);
    }
    
    @Test
    public void testAddUser() {
        this.federationMembers = new ArrayList<FederationUser>();
        this.federationServices = new ArrayList<FederationService>();
        
        this.federation = new Federation(FEDERATION_ID_1, FEDERATION_OWNER_1, 
                FEDERATION_NAME_1, FEDERATION_METADATA_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED, this.federationMembers, this.federationServices);
        
        List<FederationUser> federationUserListBefore = this.federation.getMemberList();
        
        assertEquals(0, federationUserListBefore.size());
        
        this.federation.addUser(USER_ID_TO_ADD);
        
        List<FederationUser> federationUserListAfter = this.federation.getMemberList();
        
        assertEquals(1, federationUserListAfter.size());
        FederationUser user = federationUserListAfter.get(0);
        assertEquals(USER_ID_TO_ADD, user.getName());
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
                FEDERATION_ENABLED, this.federationMembers, this.federationServices);
        
        FederationService service = Mockito.mock(FederationService.class); 

        List<FederationService> servicesBeforeRegister = this.federation.getServices();
        assertEquals(0, servicesBeforeRegister.size());
        
        this.federation.registerService(service);
        
        List<FederationService> servicesAfterRegister = this.federation.getServices();
        assertEquals(1, servicesAfterRegister.size());
        assertEquals(service, servicesAfterRegister.get(0));
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
                FEDERATION_ENABLED, this.federationMembers, this.federationServices);
        
        this.federation.getService("unregisteredServiceId");
    }
    
    @Test
    public void testGetAuthorizedServices() throws InvalidParameterException {
        List<FederationService> authorizedServicesUser1 = this.federation.getAuthorizedServices(FEDERATION_USER_ID_1);
        List<FederationService> authorizedServicesUser2 = this.federation.getAuthorizedServices(FEDERATION_USER_ID_2);
        
        assertEquals(1, authorizedServicesUser1.size());
        assertEquals(this.federationService1, authorizedServicesUser1.get(0));
        
        assertEquals(1, authorizedServicesUser2.size());
        assertEquals(this.federationService2, authorizedServicesUser2.get(0));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testGetAuthorizedServicesNotRegisteredUser() throws InvalidParameterException {
        this.federation.getAuthorizedServices("unregisteredUserId");
    }
}
