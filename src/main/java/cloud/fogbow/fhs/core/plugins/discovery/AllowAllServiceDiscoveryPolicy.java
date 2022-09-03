package cloud.fogbow.fhs.core.plugins.discovery;

import cloud.fogbow.fhs.core.models.FederationUser;

public class AllowAllServiceDiscoveryPolicy implements ServiceDiscoveryPolicy {
    private static final String ALLOW_ALL_DISCOVERY_POLICY_NAME = "allowall";

    @Override
    public boolean isDiscoverableBy(FederationUser user) {
        return true;
    }

    @Override
    public String getName() {
        return ALLOW_ALL_DISCOVERY_POLICY_NAME;
    }
}
