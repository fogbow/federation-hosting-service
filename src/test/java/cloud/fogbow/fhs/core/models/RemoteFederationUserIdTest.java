package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class RemoteFederationUserIdTest {

    @Test
    public void testEquals() {
        assertEquals(new RemoteFederationUserId("adminId1", "fhsId1"),
                new RemoteFederationUserId("adminId1", "fhsId1"));
        assertNotEquals(new RemoteFederationUserId("adminId1", "fhsId1"),
                new RemoteFederationUserId("adminId2", "fhsId1"));
        assertNotEquals(new RemoteFederationUserId("adminId1", "fhsId1"),
                new RemoteFederationUserId("adminId1", "fhsId2"));
        assertNotEquals(new RemoteFederationUserId("adminId1", "fhsId1"),
                new RemoteFederationUserId("adminId2", "fhsId2"));
    }
}
