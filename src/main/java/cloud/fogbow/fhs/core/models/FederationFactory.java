package cloud.fogbow.fhs.core.models;

import java.util.Map;

public class FederationFactory {
    public Federation createFederationFactory(String owner, String federationName, Map<String, String> metadata,
            String description, boolean enabled) {
        return new Federation(owner, federationName, metadata, description, enabled);
    }
}
