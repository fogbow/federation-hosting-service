package cloud.fogbow.fhs.core.models;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fhs.core.plugins.access.AccessPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.discovery.DiscoveryPolicyInstantiator;
import cloud.fogbow.fhs.core.plugins.invocation.ServiceInvokerInstantiator;
import cloud.fogbow.fhs.core.utils.MapUtils;

public class FederationServiceFactory {

    public FederationService createService(String ownerId, String endpoint, String discoveryPolicyClassName,
            String accessPolicyClassName, String federationId, Map<String, String> metadata) throws InvalidParameterException {
        return new FederationService(ownerId, endpoint, discoveryPolicyClassName, accessPolicyClassName, federationId,
                metadata);
    }
    
    // TODO test
    public FederationService deserialize(String str) throws InvalidParameterException {
        // FIXME constant
        String[] fields = StringUtils.splitByWholeSeparator(str, "!^!");
        
        String serviceId = fields[0];
        String ownerId = fields[1];
        String endpoint = fields[2];
        String discoveryPolicyClassName = fields[3];
        String accessPolicyClassName = fields[4];
        String federationId = fields[5];
        String metadataStr = fields[6];
        
        // FIXME constant
        if (discoveryPolicyClassName.equals("null")) {
            discoveryPolicyClassName = "";
        }

        Map<String, String> metadata = new MapUtils().deserializeMap(metadataStr);
        
        return new FederationService(serviceId, ownerId, endpoint, discoveryPolicyClassName, 
                accessPolicyClassName, federationId, metadata, 
                new DiscoveryPolicyInstantiator(), new AccessPolicyInstantiator(), 
                new ServiceInvokerInstantiator()); 
    }
}
