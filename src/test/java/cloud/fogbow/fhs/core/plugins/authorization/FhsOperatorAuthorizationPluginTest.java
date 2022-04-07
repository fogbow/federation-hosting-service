package cloud.fogbow.fhs.core.plugins.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.fhs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.fhs.constants.SystemConstants;
import cloud.fogbow.fhs.core.PropertiesHolder;
import cloud.fogbow.fhs.core.models.FhsOperation;
import cloud.fogbow.fhs.core.models.OperationType;

// TODO documentation
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesHolder.class })
public class FhsOperatorAuthorizationPluginTest {
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String OPERATOR_ID_1 = "operatorId1";
    private static final String OPERATOR_ID_2 = "operatorId2";
    private static final String OTHER_SERVICE_ADMIN_ID_1 = "otherServiceAdminId1";
    private static final String OTHER_SERVICE_ADMIN_ID_2 = "otherServiceAdminId2";
    private static final String NON_ADMIN_USER_ID_1 = "nonAdminUserId1";
    private static final List<String> FHS_OPERATOR_USER_IDS = 
            Arrays.asList(OPERATOR_ID_1, OPERATOR_ID_2);
    private static final List<String> OTHER_SERVICES_ADMIN_IDS = 
            Arrays.asList(OTHER_SERVICE_ADMIN_ID_1, OTHER_SERVICE_ADMIN_ID_2);
    private static final String OPERATOR_IDS_STRING = 
            OPERATOR_ID_1 + SystemConstants.OPERATOR_IDS_SEPARATOR + OPERATOR_ID_2;
    private static final String OTHER_SERVICES_IDS_STRING = 
            OTHER_SERVICE_ADMIN_ID_1 + SystemConstants.OPERATOR_IDS_SEPARATOR + OTHER_SERVICE_ADMIN_ID_2;
    
    private SystemUser systemUserOperator1;
    private SystemUser systemUserOperator2;
    private SystemUser systemUserOtherServiceAdmin1;
    private SystemUser systemUserOtherServiceAdmin2;
    private SystemUser systemUserNonAdmin1;
    
    private FhsOperatorAuthorizationPlugin plugin;
    
    @Before
    public void setUp() {
        this.systemUserOperator1 = Mockito.mock(SystemUser.class);
        Mockito.when(this.systemUserOperator1.getId()).thenReturn(
                OPERATOR_ID_1 + FogbowConstants.FEDERATION_ID_SEPARATOR + FEDERATION_ID_1);
        
        this.systemUserOperator2 = Mockito.mock(SystemUser.class);
        Mockito.when(this.systemUserOperator2.getId()).thenReturn(
                OPERATOR_ID_2 + FogbowConstants.FEDERATION_ID_SEPARATOR + FEDERATION_ID_1);
        
        this.systemUserOtherServiceAdmin1 = Mockito.mock(SystemUser.class);
        Mockito.when(this.systemUserOtherServiceAdmin1.getId()).thenReturn(
                OTHER_SERVICE_ADMIN_ID_1 + FogbowConstants.FEDERATION_ID_SEPARATOR + FEDERATION_ID_1);
        
        this.systemUserOtherServiceAdmin2 = Mockito.mock(SystemUser.class);
        Mockito.when(this.systemUserOtherServiceAdmin2.getId()).thenReturn(
                OTHER_SERVICE_ADMIN_ID_2 + FogbowConstants.FEDERATION_ID_SEPARATOR + FEDERATION_ID_1);
        
        this.systemUserNonAdmin1 = Mockito.mock(SystemUser.class);
        Mockito.when(this.systemUserNonAdmin1.getId()).thenReturn(
                NON_ADMIN_USER_ID_1 + FogbowConstants.FEDERATION_ID_SEPARATOR + FEDERATION_ID_1);
        
        this.plugin = new FhsOperatorAuthorizationPlugin(FHS_OPERATOR_USER_IDS, OTHER_SERVICES_ADMIN_IDS);
    }
    
    @Test
    public void testIsAuthorizedOperatorOnlyOperations() throws ConfigurationErrorException, UnauthorizedRequestException {
        for (OperationType operationType : FhsOperatorAuthorizationPlugin.OPERATOR_ONLY_OPERATIONS) {
            assertTrue(this.plugin.isAuthorized(this.systemUserOperator1, new FhsOperation(operationType)));
            assertTrue(this.plugin.isAuthorized(this.systemUserOperator2, new FhsOperation(operationType)));
            
            checkIfExceptionIsThrown(this.systemUserOtherServiceAdmin1, operationType);
            checkIfExceptionIsThrown(this.systemUserOtherServiceAdmin2, operationType);
            checkIfExceptionIsThrown(this.systemUserNonAdmin1, operationType);
        }
    }
    
    @Test
    public void testIsAuthorizedOtherServicesAdminsOnlyOperations() throws UnauthorizedRequestException {
        for (OperationType operationType : FhsOperatorAuthorizationPlugin.OTHER_SERVICES_ADMIN_ONLY_OPERATIONS) {
            assertTrue(plugin.isAuthorized(this.systemUserOperator1, new FhsOperation(operationType)));
            assertTrue(plugin.isAuthorized(this.systemUserOperator2, new FhsOperation(operationType)));
            assertTrue(plugin.isAuthorized(this.systemUserOtherServiceAdmin1, new FhsOperation(operationType)));
            assertTrue(plugin.isAuthorized(this.systemUserOtherServiceAdmin2, new FhsOperation(operationType)));
            
            checkIfExceptionIsThrown(this.systemUserNonAdmin1, operationType);
        }
    }
    
    @Test
    public void testIsAuthorizedNonAdminOperations() throws UnauthorizedRequestException {
        for (OperationType operationType : OperationType.values()) {
            if (!FhsOperatorAuthorizationPlugin.OPERATOR_ONLY_OPERATIONS.contains(operationType) 
                    && !FhsOperatorAuthorizationPlugin.OTHER_SERVICES_ADMIN_ONLY_OPERATIONS.contains(operationType)) {
                assertTrue(plugin.isAuthorized(this.systemUserOperator1, new FhsOperation(operationType)));
                assertTrue(plugin.isAuthorized(this.systemUserOperator2, new FhsOperation(operationType)));
                assertTrue(plugin.isAuthorized(this.systemUserOtherServiceAdmin1, new FhsOperation(operationType)));
                assertTrue(plugin.isAuthorized(this.systemUserOtherServiceAdmin2, new FhsOperation(operationType)));
                assertTrue(plugin.isAuthorized(this.systemUserNonAdmin1, new FhsOperation(operationType)));
            }
        }
    }
    
    @Test
    public void testConstructorReadsPropertiesCorrectly() throws ConfigurationErrorException {
        PowerMockito.mockStatic(PropertiesHolder.class);
        PropertiesHolder propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(propertiesHolder.getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(OPERATOR_IDS_STRING);
        Mockito.when(propertiesHolder.getProperty(
                ConfigurationPropertyKeys.OTHER_SERVICES_ADMIN_IDS_KEY)).thenReturn(OTHER_SERVICES_IDS_STRING);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        this.plugin = new FhsOperatorAuthorizationPlugin();
        
        List<String> operatorIds = this.plugin.getFhsOperatorUserIds();
        assertEquals(2, operatorIds.size());
        assertTrue(operatorIds.contains(OPERATOR_ID_1));
        assertTrue(operatorIds.contains(OPERATOR_ID_2));
        
        List<String> adminIds = this.plugin.getOtherServicesAdminIds();
        assertEquals(2, adminIds.size());
        assertTrue(adminIds.contains(OTHER_SERVICE_ADMIN_ID_1));
        assertTrue(adminIds.contains(OTHER_SERVICE_ADMIN_ID_2));
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfOperatorIdsStringIsEmpty() throws ConfigurationErrorException {
        PowerMockito.mockStatic(PropertiesHolder.class);
        PropertiesHolder propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(propertiesHolder.getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn("");
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        new FhsOperatorAuthorizationPlugin();
    }
    
    @Test(expected = ConfigurationErrorException.class)
    public void testInstantiationFailsIfOtherServicesAdminIdsStringIsEmpty() throws ConfigurationErrorException {
        PowerMockito.mockStatic(PropertiesHolder.class);
        PropertiesHolder propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.when(propertiesHolder.getProperty(
                ConfigurationPropertyKeys.OPERATOR_IDS_KEY)).thenReturn(OPERATOR_IDS_STRING);
        Mockito.when(propertiesHolder.getProperty(
                ConfigurationPropertyKeys.OTHER_SERVICES_ADMIN_IDS_KEY)).thenReturn("");
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        new FhsOperatorAuthorizationPlugin();
    }
    
    private void checkIfExceptionIsThrown(SystemUser systemUser, OperationType operationType) {
        try {
            plugin.isAuthorized(systemUser, new FhsOperation(operationType));
            Assert.fail("Expected exception");
        } catch (UnauthorizedRequestException e) {
            
        }
    }
}
