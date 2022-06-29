package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.utils.TestUtils;

public class FederationUserTest {
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_DESCRIPTION = "userDescription";
    private static final boolean USER_ENABLED = false;
    private static final String ATTRIBUTE_ID_1 = "attributeId1";
    private static final String ATTRIBUTE_ID_2 = "attributeId2";
    private static final String IDENTITY_PLUGIN_CLASS_NAME = "identityPluginClassName";
    private static final String FEDERATION_ID = "federationId";
    private static final String FHS_ID_1 = "fhsId1";
    private FederationUser federationUser;
    
    @Test
    public void testAddAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, USER_EMAIL, FEDERATION_ID,
                FHS_ID_1, USER_DESCRIPTION, USER_ENABLED, new ArrayList<String>(), 
                IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        assertTrue(federationUser.getAttributes().isEmpty());
        
        federationUser.addAttribute(ATTRIBUTE_ID_1);
        
        assertEquals(1, federationUser.getAttributes().size());
        assertTrue(federationUser.getAttributes().contains(ATTRIBUTE_ID_1));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAttributeAlreadyAdded() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, FEDERATION_ID, FHS_ID_1, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1), 
                IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        federationUser.addAttribute(ATTRIBUTE_ID_1);
    }
    
    @Test
    public void testRemoveAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, FEDERATION_ID, FHS_ID_1, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1), 
                IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        federationUser.removeAttribute(ATTRIBUTE_ID_1);
        
        assertTrue(federationUser.getAttributes().isEmpty());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRemoveNotFoundAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, FEDERATION_ID, FHS_ID_1, USER_EMAIL,
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1), 
                IDENTITY_PLUGIN_CLASS_NAME, new HashMap<String, String>(), false, false);
        
        federationUser.removeAttribute(ATTRIBUTE_ID_2);
    }
}
