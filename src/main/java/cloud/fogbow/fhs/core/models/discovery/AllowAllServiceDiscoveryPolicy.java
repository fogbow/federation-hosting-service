package cloud.fogbow.fhs.core.models.discovery;

import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceDiscoveryPolicy;

// TODO test
public class AllowAllServiceDiscoveryPolicy implements ServiceDiscoveryPolicy {

    @Override
    public boolean isDiscoverableBy(FederationUser user) {
        return true;
    }

    @Override
    public String getName() {
        // FIXME constant
        return "allowall";
    }
}
