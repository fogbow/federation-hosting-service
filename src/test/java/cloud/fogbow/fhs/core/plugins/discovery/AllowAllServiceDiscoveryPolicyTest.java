package cloud.fogbow.fhs.core.plugins.discovery;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import cloud.fogbow.fhs.core.models.FederationUser;


public class AllowAllServiceDiscoveryPolicyTest {

    @Test
    public void testIsDiscoverableBy() {
        FederationUser user = Mockito.mock(FederationUser.class);
        
        AllowAllServiceDiscoveryPolicy policy = new AllowAllServiceDiscoveryPolicy();
        
        assertTrue(policy.isDiscoverableBy(user));
    }
}
