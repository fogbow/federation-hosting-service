package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

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
    private FederationUser federationUser;
    
    @Test
    public void testAddAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, new ArrayList<String>());
        
        assertTrue(federationUser.getAttributes().isEmpty());
        
        federationUser.addAttribute(ATTRIBUTE_ID_1);
        
        assertEquals(1, federationUser.getAttributes().size());
        assertTrue(federationUser.getAttributes().contains(ATTRIBUTE_ID_1));
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotAddAttributeAlreadyAdded() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1));
        
        federationUser.addAttribute(ATTRIBUTE_ID_1);
    }
    
    @Test
    public void testRemoveAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1));
        
        federationUser.removeAttribute(ATTRIBUTE_ID_1);
        
        assertTrue(federationUser.getAttributes().isEmpty());
    }
    
    @Test(expected = InvalidParameterException.class)
    public void testCannotRemoveNotFoundAttribute() throws InvalidParameterException {
        federationUser = new FederationUser(USER_ID, USER_NAME, USER_EMAIL, 
                USER_DESCRIPTION, USER_ENABLED, TestUtils.getListWithElements(ATTRIBUTE_ID_1));
        
        federationUser.removeAttribute(ATTRIBUTE_ID_2);
    }
}
