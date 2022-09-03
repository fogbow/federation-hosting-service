package cloud.fogbow.fhs.core.plugins.discovery;

import cloud.fogbow.fhs.core.models.FederationUser;

public interface ServiceDiscoveryPolicy {
    String getName();
    boolean isDiscoverableBy(FederationUser user);
}
