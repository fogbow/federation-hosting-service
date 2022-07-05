package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class RemoteFederationTest {
    private static final String FEDERATION_ID_1 = "federationId1";
    private static final String FEDERATION_NAME_1 = "federationName1";
    private static final String FEDERATION_DESCRIPTION_1 = "federationDescription1";
    private static final Boolean FEDERATION_ENABLED_1 = true;
    private static final String FEDERATION_OWNER_1 = "federationOwner1";
    private static final String FEDERATION_FHS_1 = "federationFhs1";
    private static final String FEDERATION_ID_2 = "federationId2";
    private static final String FEDERATION_FHS_2 = "federationFhs2";
    private static final String FEDERATION_OWNER_2 = "federationOwner2";
    
    @Test
    public void testEquals() {
        assertEquals(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1), 
                new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                        FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1));
        
        assertNotEquals(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1), 
                new RemoteFederation(FEDERATION_ID_2, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                        FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1));
        
        assertNotEquals(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1), 
                new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                        FEDERATION_ENABLED_1, FEDERATION_OWNER_2, FEDERATION_FHS_1));
                
        assertNotEquals(new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                FEDERATION_ENABLED_1, FEDERATION_OWNER_1, FEDERATION_FHS_1), 
                new RemoteFederation(FEDERATION_ID_1, FEDERATION_NAME_1, FEDERATION_DESCRIPTION_1, 
                        FEDERATION_ENABLED_1, FEDERATION_OWNER_2, FEDERATION_FHS_2));
    }
}
