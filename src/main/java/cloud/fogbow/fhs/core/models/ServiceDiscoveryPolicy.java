package cloud.fogbow.fhs.core.models;

public interface ServiceDiscoveryPolicy {
    String getName();
    boolean isDiscoverableBy(FederationUser user);
}
