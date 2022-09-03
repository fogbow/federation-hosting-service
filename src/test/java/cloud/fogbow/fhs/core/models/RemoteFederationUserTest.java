package cloud.fogbow.fhs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class RemoteFederationUserTest {

    @Test
    public void test() {
        assertEquals(new RemoteFederationUser("adminId1", "fhsId1"),
                new RemoteFederationUser("adminId1", "fhsId1"));
        assertNotEquals(new RemoteFederationUser("adminId1", "fhsId1"),
                new RemoteFederationUser("adminId2", "fhsId1"));
        assertNotEquals(new RemoteFederationUser("adminId1", "fhsId1"),
                new RemoteFederationUser("adminId1", "fhsId2"));
        assertNotEquals(new RemoteFederationUser("adminId1", "fhsId1"),
                new RemoteFederationUser("adminId2", "fhsId2"));
    }
}
