package cloud.fogbow.fhs.core.plugins.discovery;

import cloud.fogbow.fhs.core.models.FederationUser;

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
