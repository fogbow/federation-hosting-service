package cloud.fogbow.fhs.core.intercomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.FederationHost;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.RemoteFederation;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class RemoteFacadeTest {
    private static final String FHS_ID_1 = "fhs1";
    private static final String FHS_ID_2 = "fhs2";
    private static final String FHS_ID_3 = "fhs3";
    private static final String NOT_AUTHORIZED_FHS_ID = "notAuthorizedFhsId";
    private static final String ALLOWED_FHS_IDS_STR = 
            String.join(SystemConstants.ALLOWED_FHS_IDS_SEPARATOR, FHS_ID_1, FHS_ID_2, FHS_ID_3);
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final Boolean FEDERATION_ENABLED_1 = true;
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_NAME_2 = "federationName2";
    private static final String FEDERATION_DESCRIPTION_2 = "federationDescription2";
    private static final Boolean FEDERATION_ENABLED_2 = true;
    private static final String FEDERATION_OWNER_2 = "federationOwner2";
    private static final String FEDERATION_ID_3 = "federationId3";
    private static final String FEDERATION_NAME_3 = "federationName3";
    private static final String FEDERATION_DESCRIPTION_3 = "federationDescription3";
    private static final Boolean FEDERATION_ENABLED_3 = true;
    private static final String FEDERATION_OWNER_3 = "federationOwner3";
    
    private RemoteFacade remoteFacade;
    private FederationHost federationHost;
    private PropertiesHolder propertiesHolder;
    private Federation federation1;
    private Federation federation2;
    private Federation federation3;
    private FederationUser federationUser;
    
    @Before
    public void setUp() throws InvalidParameterException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(ALLOWED_FHS_IDS_STR);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.federationHost = Mockito.mock(FederationHost.class);
        Mockito.when(this.federationHost.joinRemoteFederation(federationUser, FHS_ID_1, FEDERATION_ID_1)).thenReturn(federation1);
        
        remoteFacade = RemoteFacade.getInstance();
        
        remoteFacade.setFederationHost(federationHost);
        remoteFacade.setAllowedFhsIds(Arrays.asList(FHS_ID_1, FHS_ID_2, FHS_ID_3));
    }
    
    @Test
    public void testLoadAllowedFhsIds() throws ConfigurationErrorException {
        List<String> allowedFhsIds = RemoteFacade.loadAllowedFhsIdsOrFail();
        
        assertEquals(3, allowedFhsIds.size());
        assertTrue(allowedFhsIds.contains(FHS_ID_1));
        assertTrue(allowedFhsIds.contains(FHS_ID_2));
        assertTrue(allowedFhsIds.contains(FHS_ID_3));
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testLoadAllowedFhsIdsNullFhsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn(null);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        RemoteFacade.loadAllowedFhsIdsOrFail();
    }
    
    @Test
    public void testLoadAllowedFhsIdsEmptyFhsIdsProperty() throws ConfigurationErrorException {
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(this.propertiesHolder.getProperty(ConfigurationPropertyKeys.ALLOWED_FHS_IDS_KEY)).thenReturn("");
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        List<String> allowedFhsIds = RemoteFacade.loadAllowedFhsIdsOrFail();
        
        assertTrue(allowedFhsIds.isEmpty());
    }
    
    @Test
    public void testGetFederationList() throws FogbowException {
        this.federation1 = Mockito.mock(Federation.class);
        Mockito.when(this.federation1.getId()).thenReturn(FEDERATION_ID_1);
        Mockito.when(this.federation1.getName()).thenReturn(FEDERATION_NAME_1);
        Mockito.when(this.federation1.getDescription()).thenReturn(FEDERATION_DESCRIPTION_1);
        Mockito.when(this.federation1.enabled()).thenReturn(FEDERATION_ENABLED_1);
        Mockito.when(this.federation1.getOwner()).thenReturn(FEDERATION_OWNER_1);
        
        this.federation2 = Mockito.mock(Federation.class);
        Mockito.when(this.federation2.getId()).thenReturn(FEDERATION_ID_2);
        Mockito.when(this.federation2.getName()).thenReturn(FEDERATION_NAME_2);
        Mockito.when(this.federation2.getDescription()).thenReturn(FEDERATION_DESCRIPTION_2);
        Mockito.when(this.federation2.enabled()).thenReturn(FEDERATION_ENABLED_2);
        Mockito.when(this.federation2.getOwner()).thenReturn(FEDERATION_OWNER_2);
        
        this.federation3 = Mockito.mock(Federation.class);
        Mockito.when(this.federation3.getId()).thenReturn(FEDERATION_ID_3);
        Mockito.when(this.federation3.getName()).thenReturn(FEDERATION_NAME_3);
        Mockito.when(this.federation3.getDescription()).thenReturn(FEDERATION_DESCRIPTION_3);
        Mockito.when(this.federation3.enabled()).thenReturn(FEDERATION_ENABLED_3);
        Mockito.when(this.federation3.getOwner()).thenReturn(FEDERATION_OWNER_3);
        
        Mockito.when(this.federationHost.getFederations()).thenReturn(Arrays.asList(federation1, federation2, federation3));
        
        List<FederationInstance> federationInstances = this.remoteFacade.getFederationList(FHS_ID_1);
        
        assertEquals(3, federationInstances.size());
        assertEquals(FEDERATION_ID_1, federationInstances.get(0).getFedId());
        assertEquals(FEDERATION_NAME_1, federationInstances.get(0).getFedName());
        assertEquals(FEDERATION_DESCRIPTION_1, federationInstances.get(0).getDescription());
        assertEquals(FEDERATION_ENABLED_1, federationInstances.get(0).isEnabled());
        assertEquals(FEDERATION_OWNER_1, federationInstances.get(0).getOwningFedAdminId());
        
        assertEquals(FEDERATION_ID_2, federationInstances.get(1).getFedId());
        assertEquals(FEDERATION_NAME_2, federationInstances.get(1).getFedName());
        assertEquals(FEDERATION_DESCRIPTION_2, federationInstances.get(1).getDescription());
        assertEquals(FEDERATION_ENABLED_2, federationInstances.get(1).isEnabled());
        assertEquals(FEDERATION_OWNER_2, federationInstances.get(1).getOwningFedAdminId());
        
        assertEquals(FEDERATION_ID_3, federationInstances.get(2).getFedId());
        assertEquals(FEDERATION_NAME_3, federationInstances.get(2).getFedName());
        assertEquals(FEDERATION_DESCRIPTION_3, federationInstances.get(2).getDescription());
        assertEquals(FEDERATION_ENABLED_3, federationInstances.get(2).isEnabled());
        assertEquals(FEDERATION_OWNER_3, federationInstances.get(2).getOwningFedAdminId());
    }
    
    @Test
    public void testGetFederationListEmptyList() throws FogbowException {
        Mockito.when(this.federationHost.getFederations()).thenReturn(Arrays.asList());
        
        List<FederationInstance> federationInstances = this.remoteFacade.getFederationList(FHS_ID_1);
        
        assertTrue(federationInstances.isEmpty());
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testUnauthorizedFhsCannotGetFederationList() throws FogbowException {
        this.remoteFacade.getFederationList(NOT_AUTHORIZED_FHS_ID);
    }
    
    @Test
    public void testUpdateRemoteFederationList() throws FogbowException {
        List<FederationInstance> remoteFederationList = new ArrayList<FederationInstance>();
        remoteFederationList.add(new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, true, FEDERATION_OWNER_1));
        remoteFederationList.add(new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, true, FEDERATION_OWNER_2));
        
        List<RemoteFederation> expectedRemoteFederationList = new ArrayList<RemoteFederation>();
        expectedRemoteFederationList.add(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                true, FEDERATION_OWNER_1, FHS_ID_1));
        expectedRemoteFederationList.add(new RemoteFederation(FEDERATION_ID_2, FEDERATION_NAME_2, FEDERATION_DESCRIPTION_2, 
                true, FEDERATION_OWNER_2, FHS_ID_1));
        
        this.remoteFacade.updateRemoteFederationList(FHS_ID_1, remoteFederationList);
        
        Mockito.verify(this.federationHost).updateRemoteFederationList(Mockito.eq(FHS_ID_1), Mockito.eq(expectedRemoteFederationList));
    }
    
    @Test
    public void testUpdateRemoteFederationListEmptyList() throws FogbowException {
        List<FederationInstance> remoteFederationList = new ArrayList<FederationInstance>();
        List<RemoteFederation> expectedList = new ArrayList<RemoteFederation>();
        
        this.remoteFacade.updateRemoteFederationList(FHS_ID_1, remoteFederationList);
        
        Mockito.verify(this.federationHost).updateRemoteFederationList(Mockito.eq(FHS_ID_1), Mockito.eq(expectedList));
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testUnauthorizedFhsCannotUpdateRemoteFederationList() throws FogbowException {
        List<FederationInstance> remoteFederationList = new ArrayList<FederationInstance>();
        remoteFederationList.add(new FederationInstance(FEDERATION_ID_1, FEDERATION_NAME_1, 
                FEDERATION_DESCRIPTION_1, true, FEDERATION_OWNER_1));
        remoteFederationList.add(new FederationInstance(FEDERATION_ID_2, FEDERATION_NAME_2, 
                FEDERATION_DESCRIPTION_2, true, FEDERATION_OWNER_2));
        
        List<RemoteFederation> expectedList = new ArrayList<RemoteFederation>();
        expectedList.add(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                true, FEDERATION_OWNER_1, FHS_ID_1));
        expectedList.add(new RemoteFederation(FEDERATION_ID_2, FEDERATION_NAME_2, FEDERATION_DESCRIPTION_2, 
                true, FEDERATION_OWNER_2, FHS_ID_1));
        
        this.remoteFacade.updateRemoteFederationList(NOT_AUTHORIZED_FHS_ID, remoteFederationList);
    }
    
    @Test
    public void testJoinFederation() throws FogbowException {
        this.federationUser = Mockito.mock(FederationUser.class);
        
        Federation returnedFederation = this.remoteFacade.joinFederation(FHS_ID_1, federationUser, FEDERATION_ID_1);
        
        assertEquals(federation1, returnedFederation);
        
        Mockito.verify(this.federationHost).joinRemoteFederation(federationUser, FHS_ID_1, FEDERATION_ID_1);
    }
    
    @Test(expected = UnauthorizedRequestException.class)
    public void testUserFromUnauthorizedFhsCannotJoinFederation() throws FogbowException {
        this.remoteFacade.joinFederation(NOT_AUTHORIZED_FHS_ID, federationUser, FEDERATION_ID_1);
    }
}
