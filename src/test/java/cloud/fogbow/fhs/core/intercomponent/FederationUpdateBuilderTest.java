package cloud.fogbow.fhs.core.intercomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FederationUpdateBuilderTest {
    private static final String FEDERATION_ID = "federationId";
    private static final String NEW_FEDERATION_NAME = "federationName";
    private static final String NEW_FEDERATION_DESCRIPTION = "federationDescription";
    private static final String MEMBER_STR_1 = "memberStr1";
    private static final String MEMBER_STR_2 = "memberStr2";
    private static final String SERVICE_STR_1 = "serviceStr1";
    private static final String SERVICE_STR_2 = "serviceStr2";
    private static final String ATTRIBUTE_STR_1 = "attributeStr1";
    private static final String ATTRIBUTE_STR_2 = "attributeStr2";
    private static final String MEMBER_ID_TO_DELETE_1 = "memberIdToDelete1";
    private static final String MEMBER_ID_TO_DELETE_2 = "memberIdToDelete2";
    private static final String SERVICE_ID_TO_DELETE_1 = "serviceIdToDelete1";
    private static final String SERVICE_ID_TO_DELETE_2 = "serviceIdToDelete2";
    private static final String ATTRIBUTE_ID_TO_DELETE_1 = "attributeIdToDelete1";
    private static final String ATTRIBUTE_ID_TO_DELETE_2 = "attributeIdToDelete2";
    private static final String METADATA_KEY_1 = "metadataKey1";
    private static final String METADATA_KEY_2 = "metadataKey2";
    private static final String METADATA_VALUE_1 = "metadataValue1";
    private static final String METADATA_VALUE_2 = "metadataValue2";
    private FederationUpdateBuilder builder;
    private Map<String, String> metadata;
    
    @Test
    public void testBuild() {
        this.metadata = new HashMap<String, String>();
        this.metadata.put(METADATA_KEY_1, METADATA_VALUE_1);
        this.metadata.put(METADATA_KEY_2, METADATA_VALUE_2);
        
        this.builder = new FederationUpdateBuilder();
        
        FederationUpdate update = this.builder.
            updateFederation(FEDERATION_ID).
            withName(NEW_FEDERATION_NAME).
            withDescription(NEW_FEDERATION_DESCRIPTION).
            withMember(MEMBER_STR_1).
            withMember(MEMBER_STR_2).
            withService(SERVICE_STR_1).
            withService(SERVICE_STR_2).
            withAttribute(ATTRIBUTE_STR_1).
            withAttribute(ATTRIBUTE_STR_2).
            withMetadata(metadata).
            deleteMember(MEMBER_ID_TO_DELETE_1).
            deleteMember(MEMBER_ID_TO_DELETE_2).
            deleteService(SERVICE_ID_TO_DELETE_1).
            deleteService(SERVICE_ID_TO_DELETE_2).
            deleteAttribute(ATTRIBUTE_ID_TO_DELETE_1).
            deleteAttribute(ATTRIBUTE_ID_TO_DELETE_2).
            build();
        
        assertEquals(FEDERATION_ID, update.getTargetFederationId());
        assertEquals(NEW_FEDERATION_NAME, update.getNewName());
        assertEquals(NEW_FEDERATION_DESCRIPTION, update.getNewDescription());
        
        assertEquals(2, update.getUpdatedMembers().size());
        assertTrue(update.getUpdatedMembers().contains(MEMBER_STR_1));
        assertTrue(update.getUpdatedMembers().contains(MEMBER_STR_2));
        
        assertEquals(2, update.getUpdatedServices().size());
        assertTrue(update.getUpdatedServices().contains(SERVICE_STR_1));
        assertTrue(update.getUpdatedServices().contains(SERVICE_STR_2));
        
        assertEquals(2, update.getUpdatedAttributes().size());
        assertTrue(update.getUpdatedAttributes().contains(ATTRIBUTE_STR_1));
        assertTrue(update.getUpdatedAttributes().contains(ATTRIBUTE_STR_2));
        
        assertEquals(2, update.getMembersToDelete().size());
        assertTrue(update.getMembersToDelete().contains(MEMBER_ID_TO_DELETE_1));
        assertTrue(update.getMembersToDelete().contains(MEMBER_ID_TO_DELETE_2));
        
        assertEquals(2, update.getServicesToDelete().size());
        assertTrue(update.getServicesToDelete().contains(SERVICE_ID_TO_DELETE_1));
        assertTrue(update.getServicesToDelete().contains(SERVICE_ID_TO_DELETE_2));
        
        assertEquals(2, update.getAttributesToDelete().size());
        assertTrue(update.getAttributesToDelete().contains(ATTRIBUTE_ID_TO_DELETE_1));
        assertTrue(update.getAttributesToDelete().contains(ATTRIBUTE_ID_TO_DELETE_2));
        
        assertEquals(metadata, update.getUpdatedMetadata());
    }
}
